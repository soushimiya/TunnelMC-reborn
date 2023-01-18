package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.common.primitives.Longs;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.XboxToken;
import me.THEREALWWEFAN231.tunnelmc.utils.JoseUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@Log4j2
@UtilityClass
public class XboxAuthorization {
    private final String XBOX_DEVICE_AUTHENTICATE_URL = "https://device.auth.xboxlive.com/device/authenticate";
    private final String XBOX_AUTHORIZE_URL = "https://sisu.xboxlive.com/authorize";

    public XboxToken getXBLToken(OAuth2AccessToken liveToken, String relyingParty) {
        if(Instant.now().isAfter(Instant.now().plus(liveToken.getExpiresIn(), ChronoUnit.SECONDS))) {
            return null;
        }

        KeyPair ecdsa256KeyPair = createKeyPair();
        ECPublicKey publicKey = (ECPublicKey) ecdsa256KeyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) ecdsa256KeyPair.getPrivate();

        String deviceToken = getDeviceToken(publicKey, privateKey);
        return getXBLToken(publicKey, privateKey, liveToken, deviceToken, relyingParty);
    }

    private XboxToken getXBLToken(ECPublicKey publicKey, ECPrivateKey privateKey, OAuth2AccessToken liveToken, String deviceToken, String relyingParty) {
        ObjectNode data = JSON_MAPPER.createObjectNode()
                .put("AccessToken", "t=" + liveToken.getAccessToken())
                .put("AppId", LiveAuthorization.CLIENT_ID)
                .put("deviceToken", deviceToken)
                .put("Sandbox", "RETAIL")
                .put("UseModernGamertag", true)
                .put("SiteName", "user.auth.xboxlive.com")
                .put("RelyingParty", relyingParty)
                .set("ProofKey", getProofKey(publicKey));

        try {
            URL url = new URL(XBOX_AUTHORIZE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-xbl-contract-version", "1");
            connection.setRequestProperty("Signature", getEncodedSignature(connection, JSON_MAPPER.writeValueAsBytes(data), privateKey));

            connection.setDoOutput(true);
            JSON_MAPPER.writeValue(connection.getOutputStream(), data);

            JsonNode authorizationNode = JSON_MAPPER.readTree(connection.getInputStream())
                    .get("AuthorizationToken");
            JsonNode userInfoNode = authorizationNode
                    .get("DisplayClaims")
                    .get("xui")
                    .get(0);
            return new XboxToken(authorizationNode.get("Token").asText(), userInfoNode.get("uhs").asText());
        } catch (IOException e) {
            log.catching(e);
            return null;
        }
    }

    private String getDeviceToken(ECPublicKey publicKey, ECPrivateKey privateKey) {
        ObjectNode data = JSON_MAPPER.createObjectNode()
                .put("RelyingParty", "http://auth.xboxlive.com")
                .put("TokenType", "JWT")
                .set("Properties", JSON_MAPPER.createObjectNode()
                        .put("AuthMethod", "ProofOfPossession")
                        .put("Id", "{" + UUID.randomUUID() + "}")
                        .put("DeviceType", "Android")
                        .put("Version", "10")
                        .set("ProofKey", getProofKey(publicKey)));

        HttpURLConnection connection;
        try {
            URL url = new URL(XBOX_DEVICE_AUTHENTICATE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-xbl-contract-version", "1");
            connection.setRequestProperty("Signature", getEncodedSignature(connection, JSON_MAPPER.writeValueAsBytes(data), privateKey));

            connection.setDoOutput(true);
            JSON_MAPPER.writeValue(connection.getOutputStream(), data);

            JsonNode response = JSON_MAPPER.readTree(connection.getInputStream());
            return response.get("Token").asText();
        } catch (IOException e) {
            log.catching(e);
            return null;
        }
    }

    private ObjectNode getProofKey(ECPublicKey publicKey) {
        return JSON_MAPPER.createObjectNode()
                .put("crv", "P-256")
                .put("alg", "ES256")
                .put("use", "sig")
                .put("kty", "EC")
                .put("x", encodeProofKeyPart(publicKey.getW().getAffineX()))
                .put("y", encodeProofKeyPart(publicKey.getW().getAffineY()));
    }

    private String encodeProofKeyPart(BigInteger affinePart) {
        // So sometimes getAffineX/Y toByteArray returns 33 or 31 (really rare) bytes, and we are supposed to get 32 bytes.
        // As said in these stackoverflow answers, it says if the first byte is 0 (possibly 33 bytes?) we can then remove it.
        //https://stackoverflow.com/questions/57379134/bouncy-castle-ecc-key-pair-generation-produces-different-sizes-for-the-coordinat
        //https://stackoverflow.com/questions/4407779/biginteger-to-byte
        byte[] bigIntBytes;

        byte[] array = affinePart.toByteArray();
        if (array[0] == 0) {
            byte[] newArray = new byte[array.length - 1];
            System.arraycopy(array, 1, newArray, 0, newArray.length);
            bigIntBytes = newArray;
        }else{
            bigIntBytes = array;
        }

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bigIntBytes);
    }

    private String getEncodedSignature(HttpURLConnection httpsURLConnection, byte[] body, ECPrivateKey privateKey) throws IOException {
        long currentTime = getWindowsTimestamp();

        byte[] signatureBytes;
        try (ByteArrayOutputStream sigStream = new ByteArrayOutputStream()) {
            sigStream.write(new byte[]{0, 0, 0, 1, 0});
            sigStream.write(Longs.toByteArray(currentTime));
            sigStream.write(new byte[]{0});
            sigStream.write("POST".getBytes());
            sigStream.write(new byte[]{0});
            String query = httpsURLConnection.getURL().getQuery();
            if (query == null) {
                query = "";
            }
            sigStream.write((httpsURLConnection.getURL().getPath() + query).getBytes());
            sigStream.write(new byte[]{0});

            String authorization = httpsURLConnection.getRequestProperty("Authorization");
            if (authorization == null) {
                authorization = "";
            }
            sigStream.write(authorization.getBytes());

            sigStream.write(new byte[]{0});
            sigStream.write(body);
            sigStream.write(new byte[]{0});

            try {
                Signature signature = Signature.getInstance("SHA256withECDSA");
                signature.initSign(privateKey);
                signature.update(sigStream.toByteArray());
                signatureBytes = JoseUtils.convertDERToJOSE(signature.sign(), JoseUtils.AlgorithmType.ECDSA256);
            } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            stream.write(new byte[]{0, 0, 0, 1});
            stream.write(Longs.toByteArray(currentTime));
            stream.write(signatureBytes);

            return Base64.getEncoder().encodeToString(stream.toByteArray());
        }
    }

    // returns a Windows specific timestamp. It has a certain offset from Unix time which must be accounted for.
    private long getWindowsTimestamp() {
        return (Instant.now().getEpochSecond() + 11644473600L) * 10000000L;
    }

    private final KeyPairGenerator KEY_PAIR_GEN;
    private KeyPair createKeyPair() {
        return KEY_PAIR_GEN.generateKeyPair();
    }
    static {
        try {
            KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp256r1"));
        } catch (Exception e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }
}
