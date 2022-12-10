package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

@PacketIdentifier(HandSwingC2SPacket.class)
public class HandSwingC2STranslator extends PacketTranslator<HandSwingC2SPacket> {

	@Override
	public void translate(HandSwingC2SPacket packet, BedrockConnection bedrockConnection) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		// TODO: Find out if this is still the correct method
		AnimatePacket animatePacket = new AnimatePacket();
		animatePacket.setAction(AnimatePacket.Action.SWING_ARM);
		animatePacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
		bedrockConnection.sendPacket(animatePacket);
	}
}
