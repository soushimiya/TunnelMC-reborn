package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

@PacketIdentifier(DisconnectPacket.class)
public class DisconnectTranslator extends PacketTranslator<DisconnectPacket> {

    @Override
    public void translate(DisconnectPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        BedrockConnectionAccessor.closeConnection(!packet.isMessageSkipped() ? packet.getKickMessage() : null);
    }
}
