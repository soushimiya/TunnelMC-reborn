package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

@PacketIdentifier(RemoveEntityPacket.class)
public class RemoveEntityPacketTranslator extends PacketTranslator<RemoveEntityPacket> {

	@Override
	public void translate(RemoveEntityPacket packet, Client client) {
		int id = (int) packet.getUniqueEntityId();

		EntitiesDestroyS2CPacket entitiesDestroyS2CPacket = new EntitiesDestroyS2CPacket(id);
		client.javaConnection.processServerToClientPacket(entitiesDestroyS2CPacket);
	}
}
