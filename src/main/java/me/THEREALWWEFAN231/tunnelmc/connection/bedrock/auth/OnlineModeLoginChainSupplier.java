package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.LoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.XboxToken;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@RequiredArgsConstructor
public class OnlineModeLoginChainSupplier extends LoginChainSupplier {
    private final Consumer<String> infoCallback;

    public CompletableFuture<ChainData> get() {
        KeyPair keyPair = EncryptionUtils.createKeyPair();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        return LiveAuthorization.getAccessToken(infoCallback)
                .exceptionally(throwable -> {
                    infoCallback.accept(throwable.getMessage());
                    return null;
                })
                .thenApply(accessToken -> {
                    if(accessToken == null) {
                        return null;
                    }

                    infoCallback.accept("Login Successful! Please Wait...");
                    XboxToken xboxToken = XboxAuthorization.getXBLToken(accessToken, "https://multiplayer.minecraft.net/");
                    return MinecraftAuthentication.getMinecraftChain(publicKey, xboxToken);
                }).thenApply(chain -> {
                    if(chain == null) {
                        return null;
                    }

                    ArrayNode chainArray;
                    String x5uKey;
                    try {
                        chainArray = (ArrayNode) JSON_MAPPER.readTree(chain).get("chain");
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

                    String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                    JsonNode headerNode = JSON_MAPPER.createObjectNode()
                            .put("alg", "ES384")
                            .put("x5u", publicKeyBase64);

                    try {
                        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(headerNode));
                        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(payloadNode));
                        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                                this.signBytes(privateKey, (header + "." + payload).getBytes()));

                        String jwt = header + "." + payload + "." + signature;
                        chainArray.insert(0, jwt);
                    } catch (JsonProcessingException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }

                    return new ChainData(JSON_MAPPER.createObjectNode()
                            .set("chain", chainArray).toString(), keyPair);
                });
    }
}
