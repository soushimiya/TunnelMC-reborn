package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@PacketIdentifier(MoveEntityAbsolutePacket.class)
public class MoveEntityAbsoluteTranslator extends PacketTranslator<MoveEntityAbsolutePacket> {

	@Override
	public void translate(MoveEntityAbsolutePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
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

		float pitch = packet.getRotation().getX();
		float yaw = packet.getRotation().getY();
		float headYaw = packet.getRotation().getZ();

		boolean onGround = packet.isOnGround();

		entity.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, 3, true);
		entity.updateTrackedHeadRotation(headYaw, 3);
		entity.setOnGround(onGround);
	}
}