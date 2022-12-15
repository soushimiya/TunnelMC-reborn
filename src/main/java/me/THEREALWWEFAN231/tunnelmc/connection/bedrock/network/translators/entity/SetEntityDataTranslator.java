package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlags;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

@PacketIdentifier(SetEntityDataPacket.class)
public class SetEntityDataTranslator extends PacketTranslator<SetEntityDataPacket> {
	// TODO: Set up an entity class system, like Geyser?

	@Override
	public void translate(SetEntityDataPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int id = (int) packet.getRuntimeEntityId();

		if (TunnelMC.mc.world != null) {
			Entity entity = TunnelMC.mc.world.getEntityById(id);
			if (entity == null) {
				return;
			}
			EntityDataMap metadata = packet.getMetadata();

			for(EntityData entityData : metadata.keySet()) {
				switch (entityData) {
					case AIR_SUPPLY -> entity.setAir(metadata.getShort(entityData));
					case HEALTH -> {
						if (entity instanceof LivingEntity) {
							((LivingEntity) entity).setHealth(metadata.getInt(entityData));
						}
					}
					case NAMETAG -> bedrockConnection.displayNames.put(entity.getUuid(), metadata.getString(entityData, entity.getDisplayName().getString()));
				}
			}

			EntityFlags flags = metadata.getFlags();

			if (flags != null) {
				entity.setSneaking(flags.getFlag(EntityFlag.SNEAKING));

				if (flags.getFlag(EntityFlag.SNEAKING)) {
					entity.setPose(EntityPose.CROUCHING);
				} else {
					entity.setPose(EntityPose.STANDING);
				}
				// TODO: Climbing and swimming
			}

			EntityTrackerUpdateS2CPacket trackerUpdatePacket = new EntityTrackerUpdateS2CPacket(id, entity.getDataTracker(), true);
			javaConnection.processJavaPacket(trackerUpdatePacket);
		}
	}
}
