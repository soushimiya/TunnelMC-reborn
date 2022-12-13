package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import lombok.experimental.UtilityClass;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinClientWorld;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

import java.net.InetSocketAddress;

@UtilityClass
public class BedrockConnectionAccessor {
    private BedrockConnection currentConnection;

    public BedrockConnection createNewConnection(InetSocketAddress bindAddress, InetSocketAddress targetAddress) {
        currentConnection = new BedrockConnection(bindAddress, targetAddress);
        return getCurrentConnection();
    }

    public BedrockConnection getCurrentConnection() {
        return currentConnection;
    }

    public void closeConnection() {
        closeConnection((String) null);
    }

    public void closeConnection(Throwable throwable) {
        throwable.printStackTrace();
        closeConnection(throwable.getMessage());
    }

    public void closeConnection(String message) {
        if(!isConnectionOpen()) {
            return;
        }

        currentConnection.bedrockClient.close();
        currentConnection = null;

        if (TunnelMC.mc.world != null) {
            if(((IMixinClientWorld) TunnelMC.mc.world).getNetworkHandler().getConnection().isOpen()) {
                TunnelMC.mc.world.disconnect();
            }
        }
        if(message != null) {
            TunnelMC.mc.execute(() -> TunnelMC.mc.disconnect(
                    new DisconnectedScreen(
                            new TitleScreen(false),
                            Text.of("TunnelMC"),
                            Text.of(message)
                    )
            ));
        }
    }

    public boolean isConnectionOpen() {
        return currentConnection != null && currentConnection.bedrockClient.getRakNet() != null && currentConnection.bedrockClient.getRakNet().isRunning();
    }
}
