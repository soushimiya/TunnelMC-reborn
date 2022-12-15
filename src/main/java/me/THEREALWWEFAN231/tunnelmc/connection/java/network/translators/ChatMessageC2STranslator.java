package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@PacketIdentifier(ChatMessageC2SPacket.class)
public class ChatMessageC2STranslator extends PacketTranslator<ChatMessageC2SPacket> {

	@Override
	public void translate(ChatMessageC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		TextPacket textPacket = new TextPacket();
		textPacket.setType(TextPacket.Type.CHAT);
		textPacket.setSourceName(bedrockConnection.getAuthData().displayName());
		textPacket.setMessage(packet.chatMessage());
		textPacket.setXuid(bedrockConnection.getAuthData().xuid());

		bedrockConnection.sendPacket(textPacket);
	}
}
