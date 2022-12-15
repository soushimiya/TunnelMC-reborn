package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

@PacketIdentifier(LookAndOnGround.class)
public class LookAndOnGroundMoveC2STranslator extends PacketTranslator<PlayerMoveC2SPacket.LookAndOnGround> {

	@Override
	public void translate(LookAndOnGround packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		PlayerMoveC2STranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.HEAD_ROTATION, bedrockConnection);
	}
}
