package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;

@PacketIdentifier(NetworkStackLatencyPacket.class)
public class NetworkStackLatencyPacketTranslator extends PacketTranslator<NetworkStackLatencyPacket> {

    @Override
    public void translate(NetworkStackLatencyPacket packet, Client client) {
        if (packet.isFromServer()) {
            NetworkStackLatencyPacket resp = new NetworkStackLatencyPacket();
            resp.setTimestamp(System.currentTimeMillis());
            client.sendPacketImmediately(resp);
        }
    }
}
