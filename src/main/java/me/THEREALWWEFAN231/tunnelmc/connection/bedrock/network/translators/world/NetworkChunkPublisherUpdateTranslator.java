package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;

@PacketIdentifier(NetworkChunkPublisherUpdatePacket.class)
public class NetworkChunkPublisherUpdateTranslator extends PacketTranslator<NetworkChunkPublisherUpdatePacket> {

    @Override
    public void translate(NetworkChunkPublisherUpdatePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        ChunkRenderDistanceCenterS2CPacket renderDistanceCenterPacket = new ChunkRenderDistanceCenterS2CPacket(
                packet.getPosition().getX() >> 4, packet.getPosition().getZ() >> 4);
        javaConnection.processJavaPacket(renderDistanceCenterPacket);
    }
}
