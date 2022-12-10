package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;

@PacketIdentifier(ChunkRadiusUpdatedPacket.class)
public class ChunkRadiusUpdatedTranslator extends PacketTranslator<ChunkRadiusUpdatedPacket> {

	@Override
	public void translate(ChunkRadiusUpdatedPacket packet, BedrockConnection bedrockConnection) {
		bedrockConnection.javaConnection.processServerToClientPacket(new ChunkLoadDistanceS2CPacket(packet.getRadius()));
	}
}
