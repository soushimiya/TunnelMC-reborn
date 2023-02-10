package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

@PacketIdentifier(RemoveEntityPacket.class)
public class RemoveEntityTranslator extends PacketTranslator<RemoveEntityPacket> {

	@Override
	public void translate(RemoveEntityPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		EntitiesDestroyS2CPacket entitiesDestroyS2CPacket = new EntitiesDestroyS2CPacket((int) packet.getUniqueEntityId());
		javaConnection.processJavaPacket(entitiesDestroyS2CPacket);
	}
}
