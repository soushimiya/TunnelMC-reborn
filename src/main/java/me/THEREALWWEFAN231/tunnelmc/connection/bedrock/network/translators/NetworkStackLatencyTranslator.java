package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@PacketIdentifier(NetworkStackLatencyPacket.class)
public class NetworkStackLatencyTranslator extends PacketTranslator<NetworkStackLatencyPacket> {

    @Override
    public void translate(NetworkStackLatencyPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (packet.isFromServer()) {
            NetworkStackLatencyPacket resp = new NetworkStackLatencyPacket();
            resp.setTimestamp(System.currentTimeMillis());
            bedrockConnection.sendPacketImmediately(resp);
        }
    }
}
