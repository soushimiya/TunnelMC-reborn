package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;

@UtilityClass
public class BedrockConnectionAccessor {
    private Client currentConnection;

    public Client createNewConnection(InetSocketAddress bindAddress) {
        currentConnection = new Client(bindAddress);
        return getCurrentConnection();
    }

    public Client getCurrentConnection() {
        return currentConnection;
    }

    public boolean isConnectionOpen() {
        return currentConnection != null && currentConnection.bedrockClient.getRakNet() != null && currentConnection.bedrockClient.getRakNet().isRunning();
    }
}
