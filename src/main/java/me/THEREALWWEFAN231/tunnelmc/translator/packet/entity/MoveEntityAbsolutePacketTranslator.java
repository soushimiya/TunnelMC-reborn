package me.THEREALWWEFAN231.tunnelmc.translator.packet.entity;

import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;

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

		short deltaX = (short) ((x * 32 - entity.getX() * 32) * 128);
		short deltaY = (short) ((y * 32 - entity.getY() * 32) * 128);
		short deltaZ = (short) ((z * 32 - entity.getZ() * 32) * 128);

		EntityS2CPacket.RotateAndMoveRelative entityPositionS2CPacket =
				new EntityS2CPacket.RotateAndMoveRelative(id, deltaX, deltaY, deltaZ, yaw, pitch, onGround);

		Client.instance.javaConnection.processServerToClientPacket(entityPositionS2CPacket);
		Client.instance.javaConnection.processServerToClientPacket(new EntitySetHeadYawS2CPacket(entity, headYaw));
	}

	@Override
	public Class<MoveEntityAbsolutePacket> getPacketClass() {
		return MoveEntityAbsolutePacket.class;
	}
}