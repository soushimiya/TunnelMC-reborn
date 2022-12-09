package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.XboxToken;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@UtilityClass
public class MinecraftAuthentication {
    private final String MINECRAFT_AUTHENTICATE_URL = "https://multiplayer.minecraft.net/authentication";

    public String getMinecraftChain(ECPublicKey publicKey, XboxToken xboxToken) {
        ObjectNode data = JSON_MAPPER.createObjectNode()
                .put("identityPublicKey", Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        try {
            URL url = new URL(MINECRAFT_AUTHENTICATE_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("x-xbl-contract-version", "1");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", xboxToken.header());
            connection.setRequestProperty("User-Agent", "MCPE/Android");
            connection.setRequestProperty("Client-Version", Client.CODEC.getMinecraftVersion());

            connection.setDoOutput(true);
            JSON_MAPPER.writeValue(connection.getOutputStream(), data);

            return new String(connection.getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
