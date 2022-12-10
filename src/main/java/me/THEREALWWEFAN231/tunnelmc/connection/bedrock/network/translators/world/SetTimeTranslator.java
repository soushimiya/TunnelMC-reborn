package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.SetTimePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@PacketIdentifier(SetTimePacket.class)
public class SetTimeTranslator extends PacketTranslator<SetTimePacket> {

	@Override
	public void translate(SetTimePacket packet, BedrockConnection bedrockConnection) {
		WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket = new WorldTimeUpdateS2CPacket(packet.getTime(), packet.getTime(), true);//TODO: remove true and replace it with the gamerule
		bedrockConnection.javaConnection.processServerToClientPacket(worldTimeUpdateS2CPacket);
	}
	
	@Override
	public boolean idleUntil() {
		return TunnelMC.mc.world == null;
	}
}
