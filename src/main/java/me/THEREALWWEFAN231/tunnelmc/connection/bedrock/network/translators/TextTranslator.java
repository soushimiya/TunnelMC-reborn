package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

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
			case POPUP:
			case JUKEBOX_POPUP:
			case TRANSLATION: {
				if(packet.getParameters().size() == 0) {
					if(packet.getType() == TextPacket.Type.TRANSLATION) {
						packet.setType(TextPacket.Type.RAW);
					}else{
						packet.setType(TextPacket.Type.TIP);
					}

					this.translateType(packet, bedrockConnection, javaConnection);
					return;
				}

				TextComponent component = LegacyComponentSerializer.legacySection().deserialize(packet.getMessage().replaceAll("%", ""));
				String cleaned = component.style(Style.empty()).content();
				// TODO: translate translation keys, ironic

				Component translatableComponent = Component.translatable(cleaned, component.color())
						.args(packet.getParameters().stream().map(Component::text).collect(Collectors.toList()));
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(TunnelMC.ADVENTURE.toNative(translatableComponent),
						packet.getType() != TextPacket.Type.TRANSLATION);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case TIP: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(packet.getMessage()), true);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
		}
	}
}
