package me.THEREALWWEFAN231.tunnelmc.translator.packet.entity;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class MovePlayerPacketTranslator extends PacketTranslator<MovePlayerPacket> {
	private static final AtomicInteger teleportId = new AtomicInteger(1);

	@Override
	public void translate(MovePlayerPacket packet) {
		int id = (int) packet.getRuntimeEntityId();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY() - 1.62;
		double z = packet.getPosition().getZ();

		float realHeadYaw = packet.getRotation().getZ();
		byte headYaw = (byte) ((int) (realHeadYaw * 256.0F / 360.0F));
		float realYaw = packet.getRotation().getY();
		byte yaw = (byte) ((int) (realYaw * 256.0F / 360.0F));
		float realPitch = packet.getRotation().getX();
		byte pitch = (byte) ((int) (realPitch * 256.0F / 360.0F));
		boolean onGround = packet.isOnGround();

		if (id == TunnelMC.mc.player.getId()) {
			// This works best
			PlayerPositionLookS2CPacket positionPacket = new PlayerPositionLookS2CPacket(x, y, z, yaw, pitch, Collections.emptySet(), teleportId.getAndIncrement(), false);
			Client.instance.javaConnection.processServerToClientPacket(positionPacket);
			return;
		}
		if (TunnelMC.mc.world == null) {
			return;
		}

		Entity entity = TunnelMC.mc.world.getEntityById(id);
		if (entity == null) {
			return;
		}

		short deltaX = (short) ((x * 32 - entity.prevX * 32) * 128);
		short deltaY = (short) ((y * 32 - entity.prevY * 32) * 128);
		short deltaZ = (short) ((z * 32 - entity.prevZ * 32) * 128);

		EntityS2CPacket.RotateAndMoveRelative entityPositionS2CPacket =
				new EntityS2CPacket.RotateAndMoveRelative(id, deltaX, deltaY, deltaZ, yaw, pitch, onGround);

		Client.instance.javaConnection.processServerToClientPacket(entityPositionS2CPacket);
		Client.instance.javaConnection.processServerToClientPacket(new EntitySetHeadYawS2CPacket(entity, headYaw));
	}

	@Override
	public Class<MovePlayerPacket> getPacketClass() {
		return MovePlayerPacket.class;
	}
	
	@Override
	public boolean idleUntil() {
		return TunnelMC.mc.player == null;
	}
}
