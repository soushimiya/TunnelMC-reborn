package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.THEREALWWEFAN231.tunnelmc.utils.JoseUtils;

import java.io.IOException;
import java.security.*;
import java.util.Base64;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

public record ChainData(String rawData, KeyPair keyPair) {

    public PublicKey publicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey privateKey() {
        return keyPair.getPrivate();
    }

    public byte[] signBytes(byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA384withECDSA");
        signature.initSign(privateKey());
        signature.update(dataToSign);

        return JoseUtils.convertDERToJOSE(signature.sign(), JoseUtils.AlgorithmType.ECDSA384);
    }

    // This is meant to be stored afterwards
    public AuthData decodeAuthData() {
        try {
            ArrayNode node = (ArrayNode) JSON_MAPPER.readTree(rawData).get("chain");

            switch (node.size()) {
                case 1 -> {
                    // Self-signed
                    JsonNode chainPart = decodeChainPart(node.get(0).asText().split("\\."));
                    return JSON_MAPPER.convertValue(chainPart.get("extraData"), AuthData.class);
                }
                case 3 -> {
                    // Assuming Mojang signed chain
                    JsonNode chainPart = decodeChainPart(node.get(2).asText().split("\\."));
                    return JSON_MAPPER.convertValue(chainPart.get("extraData"), AuthData.class);
                }
                default -> throw new IndexOutOfBoundsException("Invalid chain size");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode decodeChainPart(String[] parts) throws IOException {
        if(parts.length != 3) {
            throw new RuntimeException("Invalid chain");
        }

        return JSON_MAPPER.readTree(Base64.getUrlDecoder().decode(parts[1]));
    }
}
