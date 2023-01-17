package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.LoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.XboxToken;
import me.THEREALWWEFAN231.tunnelmc.utils.exceptions.TokenException;
import net.minecraft.text.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@Log4j2
@RequiredArgsConstructor
public class OnlineModeLoginChainSupplier extends LoginChainSupplier {
    protected final Consumer<Text> infoCallback;
    protected final File rememberAccountFile;

    public OnlineModeLoginChainSupplier() {
        this(s -> {});
    }

    public OnlineModeLoginChainSupplier(File rememberAccountFile) {
        this(s -> {}, rememberAccountFile);
    }

    public OnlineModeLoginChainSupplier(Consumer<Text> infoCallback) {
        this(infoCallback, null);
    }

    public CompletableFuture<ChainData> get() {
        CompletableFuture<ChainData> future = new CompletableFuture<>();

        DeviceAuthorization authorization = LiveAuthorization.INSTANCE.getAccessToken(accessToken -> {
            if(this.rememberAccountFile != null) {
                try {
                    JSON_MAPPER.writeValue(this.rememberAccountFile, accessToken);
                } catch (IOException e) {
                    log.error(e);
                }
            }

            ChainData chainData = this.getChain(accessToken);
            if(chainData == null) {
                future.completeExceptionally(new TokenException());
                return;
            }

            future.complete(chainData);
        });
        future.whenComplete((chainData, throwable) -> {
            if(throwable != null) {
                LiveAuthorization.INSTANCE.cancel(authorization.getUserCode());
            }
        });
        
        MutableText text = Text.literal("Authenticate at ");
        MutableText urlText = Text.literal(authorization.getVerificationUri());
        try {
            URL clickUrl = new URL(authorization.getVerificationUri() + "?otc=" + authorization.getUserCode());
            urlText.setStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickUrl.toString()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Opens: " + clickUrl)))
                    .withUnderline(true));
        } catch (MalformedURLException ignored) {}
        text.append(urlText);

        infoCallback.accept(text.append(Text.literal(" with code " + authorization.getUserCode())));
        return future;
    }

    protected ChainData getChain(OAuth2AccessToken accessToken) {
        if(accessToken == null) {
            return null;
        }

        KeyPair keyPair = EncryptionUtils.createKeyPair();
        ArrayNode selfSignedChain = getSelfSignedChain(getAuthenticatedChain(accessToken, keyPair.getPublic()), keyPair);
        if(selfSignedChain == null) {
            return null;
        }

        return new ChainData(JSON_MAPPER.createObjectNode().set("chain", selfSignedChain).toString(), keyPair);
    }

    private String getAuthenticatedChain(OAuth2AccessToken token, PublicKey publicKey) {
        infoCallback.accept(Text.of("Microsoft login successful! Please wait..."));
        XboxToken xboxToken = XboxAuthorization.getXBLToken(token, "https://multiplayer.minecraft.net/");
        if(xboxToken == null) {
            infoCallback.accept(Text.of("Xbox login unsuccessful. Please try and login again."));
            return null;
        }
        infoCallback.accept(Text.of("Xbox login successful! Please wait..."));
        String minecraftChain = MinecraftAuthentication.getMinecraftChain(publicKey, xboxToken);
        if(minecraftChain == null) {
            infoCallback.accept(Text.of("Minecraft login unsuccessful. Please try and login again."));
            return null;
        }
        infoCallback.accept(Text.of("Minecraft login successful! Please wait..."));
        return minecraftChain;
    }

    private ArrayNode getSelfSignedChain(String authenticatedChain, KeyPair keyPair) {
        if(authenticatedChain == null) {
            return null;
        }

        ArrayNode chainArray;
        String x5uKey;
        try {
            chainArray = (ArrayNode) JSON_MAPPER.readTree(authenticatedChain).get("chain");
            String[] parts = chainArray.get(0).asText().split("\\.");

            JsonNode headerNode = JSON_MAPPER.readTree(Base64.getUrlDecoder().decode(parts[0]));
            x5uKey = headerNode.get("x5u").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonNode payloadNode = JSON_MAPPER.createObjectNode()
                .put("certificateAuthority", true)
                .put("exp", Instant.now().plus(6, ChronoUnit.HOURS).getEpochSecond())
                .put("identityPublicKey", x5uKey)
                .put("nbf", Instant.now().minus(6, ChronoUnit.HOURS).getEpochSecond());

        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        JsonNode headerNode = JSON_MAPPER.createObjectNode()
                .put("alg", "ES384")
                .put("x5u", publicKeyBase64);

        try {
            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(headerNode));
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(payloadNode));
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    this.signBytes(keyPair.getPrivate(), (header + "." + payload).getBytes()));

            String jwt = header + "." + payload + "." + signature;
            chainArray.insert(0, jwt);
        } catch (JsonProcessingException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return chainArray;
    }
}
