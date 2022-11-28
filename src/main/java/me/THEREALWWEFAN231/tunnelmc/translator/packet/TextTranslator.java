package me.THEREALWWEFAN231.tunnelmc.translator.packet;

import com.nukkitx.protocol.bedrock.packet.TextPacket;

import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

public class TextTranslator extends PacketTranslator<TextPacket> {

	@Override
	public void translate(TextPacket packet) {
		switch (packet.getType()) {
			default: {
				System.out.println("Falling back to raw translation for " + packet.toString());
			}
			case RAW: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(packet.getMessage()), false);
				Client.instance.javaConnection.processServerToClientPacket(gameMessageS2CPacket);
				break;
			}
			case CHAT: {
				String formattedChatMessage = packet.getMessage();
				if (packet.getSourceName().length() > 0) {
					formattedChatMessage = "<" + packet.getSourceName() + "> " + formattedChatMessage;
				}

				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(formattedChatMessage), false);
				Client.instance.javaConnection.processServerToClientPacket(gameMessageS2CPacket);
				break;
			}
		}
	}

	@Override
	public Class<TextPacket> getPacketClass() {
		return TextPacket.class;
	}
}
