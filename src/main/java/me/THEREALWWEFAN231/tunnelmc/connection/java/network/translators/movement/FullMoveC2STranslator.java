package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

@PacketIdentifier(Full.class)
public class FullMoveC2STranslator extends PacketTranslator<Full> {

	@Override
	public void translate(Full packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		PlayerMoveC2STranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL, bedrockConnection);
	}
}
