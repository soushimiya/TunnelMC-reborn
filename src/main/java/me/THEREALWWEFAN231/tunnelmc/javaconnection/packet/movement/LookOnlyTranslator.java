package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;

import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

public class LookOnlyTranslator extends PacketTranslator<PlayerMoveC2SPacket.LookAndOnGround> {

	@Override
	public void translate(LookAndOnGround packet) {
		PlayerMoveTranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.HEAD_ROTATION);
	}

	@Override
	public Class<LookAndOnGround> getPacketClass() {
		return PlayerMoveC2SPacket.LookAndOnGround.class;
	}
}
