package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

@PacketIdentifier(PlayStatusPacket.class)
public class PlayStatusTranslator extends PacketTranslator<PlayStatusPacket> {
    @Override
    public void translate(PlayStatusPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if(packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(bedrockConnection.entityRuntimeId);
            bedrockConnection.sendPacketImmediately(setLocalPlayerAsInitializedPacket);
        }
    }
}
