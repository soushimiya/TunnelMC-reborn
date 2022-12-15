package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;

@PacketIdentifier(ChunkRadiusUpdatedPacket.class)
public class ChunkRadiusUpdatedTranslator extends PacketTranslator<ChunkRadiusUpdatedPacket> {

	@Override
	public void translate(ChunkRadiusUpdatedPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		javaConnection.processJavaPacket(new ChunkLoadDistanceS2CPacket(packet.getRadius()));
	}
}
