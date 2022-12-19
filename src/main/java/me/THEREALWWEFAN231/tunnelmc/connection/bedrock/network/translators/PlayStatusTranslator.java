package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import com.nukkitx.protocol.bedrock.packet.TickSyncPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

import java.time.Instant;

@PacketIdentifier(PlayStatusPacket.class)
public class PlayStatusTranslator extends PacketTranslator<PlayStatusPacket> {
    @Override
    public void translate(PlayStatusPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if(packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            TickSyncPacket tickSyncPacket = new TickSyncPacket();
            tickSyncPacket.setRequestTimestamp(Instant.now().toEpochMilli());
            bedrockConnection.sendPacketImmediately(tickSyncPacket);

            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(bedrockConnection.entityRuntimeId);
            bedrockConnection.sendPacketImmediately(setLocalPlayerAsInitializedPacket);
        }
    }
}
