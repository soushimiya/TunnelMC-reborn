package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@PacketIdentifier(ChatMessageC2SPacket.class)
public class ChatMessageC2STranslator extends PacketTranslator<ChatMessageC2SPacket> {

	@Override
	public void translate(ChatMessageC2SPacket packet, Client client) {
		TextPacket textPacket = new TextPacket();
		textPacket.setType(TextPacket.Type.CHAT);
		textPacket.setSourceName(client.authData.displayName());
		textPacket.setMessage(packet.chatMessage());
		textPacket.setXuid(client.authData.xuid());

		client.sendPacket(textPacket);
	}
}
