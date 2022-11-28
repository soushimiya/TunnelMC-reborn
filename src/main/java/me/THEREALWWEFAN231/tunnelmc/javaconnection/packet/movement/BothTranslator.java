package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;

import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

public class BothTranslator extends PacketTranslator<Full> {

	@Override
	public void translate(Full packet) {
		PlayerMoveTranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL);
	}

	@Override
	public Class<?> getPacketClass() {
		return PlayerMoveC2SPacket.Full.class;
	}

}
