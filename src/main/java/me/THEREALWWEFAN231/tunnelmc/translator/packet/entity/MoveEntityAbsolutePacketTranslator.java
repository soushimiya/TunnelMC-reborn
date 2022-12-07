package me.THEREALWWEFAN231.tunnelmc.translator.packet.entity;

import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class MoveEntityAbsolutePacketTranslator extends PacketTranslator<MoveEntityAbsolutePacket> {

	@Override
	public void translate(MoveEntityAbsolutePacket packet) {
		if (TunnelMC.mc.world == null) {
			return;
		}

		int id = (int) packet.getRuntimeEntityId();
		Entity entity = TunnelMC.mc.world.getEntityById(id);
		if (entity == null) {
			return;
		}

		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY();
		double z = packet.getPosition().getZ();
		if(entity instanceof PlayerEntity) {
			y -= 1.62;
		}

		float realHeadYaw = packet.getRotation().getZ();
		byte headYaw = (byte) ((int) (realHeadYaw * 256.0F / 360.0F));
		float realYaw = packet.getRotation().getY();
		byte yaw = (byte) ((int) (realYaw * 256.0F / 360.0F));
		float realPitch = packet.getRotation().getX();
		byte pitch = (byte) ((int) (realPitch * 256.0F / 360.0F));
		boolean onGround = packet.isOnGround();

		entity.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, 3, true);
		entity.setHeadYaw(headYaw);
		entity.setOnGround(onGround);
	}

	@Override
	public Class<MoveEntityAbsolutePacket> getPacketClass() {
		return MoveEntityAbsolutePacket.class;
	}
}