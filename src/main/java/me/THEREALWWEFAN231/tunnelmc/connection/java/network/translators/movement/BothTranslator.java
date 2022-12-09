package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

@PacketIdentifier(Full.class)
public class BothTranslator extends PacketTranslator<Full> {

	@Override
	public void translate(Full packet, Client client) {
		PlayerMoveTranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL, client);
	}
}
