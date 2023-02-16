package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.LoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.AuthData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@RequiredArgsConstructor
public class OfflineModeLoginChainSupplier extends LoginChainSupplier {
    private final String username;

    @Override
    public CompletableFuture<ChainData> get() {
        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

        KeyPair keyPair = EncryptionUtils.createKeyPair();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        ObjectNode chain = JSON_MAPPER.createObjectNode()
                .put("exp", Instant.now().plus(6, ChronoUnit.HOURS).getEpochSecond())
                .put("identityPublicKey", publicKeyBase64)
                .put("nbf", Instant.now().minus(6, ChronoUnit.HOURS).getEpochSecond())
                .putPOJO("extraData", AuthData.offlineMode(username, offlineUUID));

        ObjectNode jwtHeader = JSON_MAPPER.createObjectNode()
                .put("alg", "ES384")
                .put("x5u", publicKeyBase64);

        String jwt;
        try {
            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(jwtHeader));
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(chain));
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    this.signBytes(privateKey, (header + "." + payload).getBytes()));

            jwt = header + "." + payload + "." + signature;
        } catch (JsonProcessingException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(new ChainData(JSON_MAPPER.createObjectNode()
                .set("chain", JSON_MAPPER.createArrayNode().add(jwt)).toString(), keyPair));
    }
}
