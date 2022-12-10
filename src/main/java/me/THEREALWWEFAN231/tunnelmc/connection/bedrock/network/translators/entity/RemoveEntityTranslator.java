package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

@PacketIdentifier(RemoveEntityPacket.class)
public class RemoveEntityTranslator extends PacketTranslator<RemoveEntityPacket> {

	@Override
	public void translate(RemoveEntityPacket packet, BedrockConnection bedrockConnection) {
		int id = (int) packet.getUniqueEntityId();

		EntitiesDestroyS2CPacket entitiesDestroyS2CPacket = new EntitiesDestroyS2CPacket(id);
		bedrockConnection.javaConnection.processServerToClientPacket(entitiesDestroyS2CPacket);
	}
}
