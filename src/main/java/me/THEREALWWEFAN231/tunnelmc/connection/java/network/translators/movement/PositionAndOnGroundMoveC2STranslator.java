package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

@PacketIdentifier(PositionAndOnGround.class)
public class PositionAndOnGroundMoveC2STranslator extends PacketTranslator<PlayerMoveC2SPacket.PositionAndOnGround> {

	@Override
	public void translate(PositionAndOnGround packet, Client client) {
		PlayerMoveC2STranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL, client);
	}
}
