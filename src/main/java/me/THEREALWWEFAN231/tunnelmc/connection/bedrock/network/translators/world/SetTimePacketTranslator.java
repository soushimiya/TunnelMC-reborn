package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.SetTimePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@PacketIdentifier(SetTimePacket.class)
public class SetTimePacketTranslator extends PacketTranslator<SetTimePacket> {

	@Override
	public void translate(SetTimePacket packet, Client client) {
		WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket = new WorldTimeUpdateS2CPacket(packet.getTime(), packet.getTime(), true);//TODO: remove true and replace it with the gamerule
		client.javaConnection.processServerToClientPacket(worldTimeUpdateS2CPacket);
	}
	
	@Override
	public boolean idleUntil() {
		return TunnelMC.mc.world == null;
	}
}
