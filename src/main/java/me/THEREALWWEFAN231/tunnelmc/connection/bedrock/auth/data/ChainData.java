package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.THEREALWWEFAN231.tunnelmc.utils.JoseUtils;

import java.security.*;

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
        ArrayNode node;
        try {
            node = (ArrayNode) JSON_MAPPER.readTree(rawData).get("chain");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(rawData);
        switch (node.size()) {
            case 1 -> {
                // first node
            }
            case 3 -> {
                // last node
            }
            default -> throw new IndexOutOfBoundsException("Invalid chain size");
        }
        return null;
    }
}
