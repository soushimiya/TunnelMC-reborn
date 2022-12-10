package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.EntityTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;

@PacketIdentifier(AddEntityPacket.class)
public class AddEntityTranslator extends PacketTranslator<AddEntityPacket> {
	// TODO: Handle non living entities differently, with the EntitySpawnS2CPacket.

	@Override
	public void translate(AddEntityPacket packet, BedrockConnection bedrockConnection) {
		EntityType<?> entityType = EntityTranslator.BEDROCK_IDENTIFIER_TO_ENTITY_TYPE.get(packet.getIdentifier());
		if (entityType == null) {
			System.out.println("Could not find entity type: " + packet.getIdentifier());
			return;
		}

		int id = (int) packet.getUniqueEntityId();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY();
		double z = packet.getPosition().getZ();
		double motionX = packet.getMotion().getX();
		double motionY = packet.getMotion().getY();
		double motionZ = packet.getMotion().getZ();
		float pitch = packet.getRotation().getX();
		float yaw = packet.getRotation().getY();
		float headYaw = packet.getRotation().getZ();

		Entity entity = entityType.create(TunnelMC.mc.world);
		if (entity == null) {
			System.out.println("Could not create entity type: " + packet.getIdentifier());
			return;
		}

		entity.setId(id);
		entity.setPos(x, y, z);
		entity.setVelocity(motionX, motionY, motionZ);
		entity.setYaw(yaw);
		entity.setHeadYaw(headYaw);
		entity.setPitch(pitch);

		bedrockConnection.javaConnection.processServerToClientPacket((Packet<ClientPlayPacketListener>) entity.createSpawnPacket());
	}
}
