package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.SetEntityMotionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

@PacketIdentifier(SetEntityMotionPacket.class)
public class SetEntityMotionTranslator extends PacketTranslator<SetEntityMotionPacket>{

	@Override
	public void translate(SetEntityMotionPacket packet, Client client) {
		if (TunnelMC.mc.world == null) {
			return;
		}
		int id = (int) packet.getRuntimeEntityId();
		Vec3d velocity = new Vec3d(packet.getMotion().getX(), packet.getMotion().getY(), packet.getMotion().getZ());
		
		EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket = new EntityVelocityUpdateS2CPacket(id, velocity);
		client.javaConnection.processServerToClientPacket(entityVelocityUpdateS2CPacket);
	}
}
