package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@PacketIdentifier(MovePlayerPacket.class)
public class MovePlayerTranslator extends PacketTranslator<MovePlayerPacket> {
	private static final AtomicInteger teleportId = new AtomicInteger(1);

	@Override
	public void translate(MovePlayerPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int id = (int) packet.getRuntimeEntityId();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY() - 1.62;
		double z = packet.getPosition().getZ();

		float pitch = packet.getRotation().getX();
		float yaw = packet.getRotation().getY();
		float headYaw = packet.getRotation().getZ();

		boolean onGround = packet.isOnGround();

		if (id == TunnelMC.mc.player.getId()) {
			// This works best
			PlayerPositionLookS2CPacket positionPacket = new PlayerPositionLookS2CPacket(x, y, z, yaw, pitch, Collections.emptySet(), teleportId.getAndIncrement(), false);
			javaConnection.processJavaPacket(positionPacket);
			return;
		}
		if (TunnelMC.mc.world == null) {
			return;
		}

		Entity entity = TunnelMC.mc.world.getEntityById(id);
		if (entity == null) {
			return;
		}

		entity.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, 3, true);
		entity.updateTrackedHeadRotation(headYaw, 3);
		entity.setOnGround(onGround);
	}
	
	@Override
	public boolean idleUntil() {
		return TunnelMC.mc.player == null;
	}
}
