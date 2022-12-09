package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;

@PacketIdentifier(DisconnectPacket.class)
public class DisconnectTranslator extends PacketTranslator<DisconnectPacket> {

    @Override
    public void translate(DisconnectPacket packet, Client client) {
        BedrockConnectionAccessor.closeConnection(!packet.isMessageSkipped() ? packet.getKickMessage() : null);
    }
}
