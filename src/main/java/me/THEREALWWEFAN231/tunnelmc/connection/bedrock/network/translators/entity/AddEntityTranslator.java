package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import it.unimi.dsi.fastutil.Pair;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.EntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

@Log4j2
@PacketIdentifier(AddEntityPacket.class)
public class AddEntityTranslator extends PacketTranslator<AddEntityPacket> {
	// TODO: Handle non living entities differently, with the EntitySpawnS2CPacket.

	@Override
	public void translate(AddEntityPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		EntityType<?> entityType = EntityTranslator.BEDROCK_IDENTIFIER_TO_ENTITY_TYPE.get(packet.getIdentifier());
		if (entityType == null) {
			log.error("Could not find entity type: " + packet.getIdentifier());
			return;
		}

		int id = (int) packet.getUniqueEntityId();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY();
		double z = packet.getPosition().getZ();
		float pitch = packet.getRotation().getX();
		float yaw = packet.getRotation().getY();
		float headYaw = packet.getRotation().getZ();
		Vec3d velocity = new Vec3d(packet.getMotion().getX(), packet.getMotion().getY(), packet.getMotion().getZ());

		TunnelMC.mc.executeSync(() -> {
			Entity entity = entityType.create(TunnelMC.mc.world);
			if (entity == null) {
				log.error("Could not create entity type: " + packet.getIdentifier());
				return;
			}

			entity.setId(id);
			entity.setPos(x, y, z);
			entity.setYaw(yaw);
			entity.setHeadYaw(headYaw);
			entity.setPitch(pitch);
			entity.setVelocity(velocity);

			javaConnection.processJavaPacket((Packet<ClientPlayPacketListener>) entity.createSpawnPacket());

			bedrockConnection.getEntityMetadataTranslatorManager().translateData(Pair.of(entity, packet.getMetadata()), bedrockConnection, javaConnection);

			EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket = new EntityTrackerUpdateS2CPacket(id, entity.getDataTracker(), true);
			javaConnection.processJavaPacket(entityTrackerUpdateS2CPacket);
		});
	}
}
