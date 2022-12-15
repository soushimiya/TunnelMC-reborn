package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

@Log4j2
@PacketIdentifier(TextPacket.class)
public class TextTranslator extends PacketTranslator<TextPacket> {

	@Override
	public void translate(TextPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		switch (packet.getType()) {
			default: {
				log.debug("Falling back to raw translation for " + packet);
			}
			case RAW: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(packet.getMessage()), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case CHAT: {
				String formattedChatMessage = packet.getMessage();
				if (packet.getSourceName().length() > 0) {
					formattedChatMessage = "<" + packet.getSourceName() + "> " + formattedChatMessage;
				}

				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(formattedChatMessage), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case TRANSLATION: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.translatable(packet.getMessage().replaceAll("%", ""), packet.getParameters()), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case POPUP:
			case TIP: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(packet.getMessage()), true);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
		}
	}
}
