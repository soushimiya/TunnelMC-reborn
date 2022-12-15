package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslatorManager;

import java.util.Collection;

@Log4j2
@RequiredArgsConstructor
public class ClientBatchHandler implements BatchHandler {
	private final BedrockConnection bedrockConnection;
	private final FakeJavaConnection javaConnection;
	private final PacketTranslatorManager<BedrockPacket> packetTranslatorManager;

	public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
		for (BedrockPacket packet : packets) {
			if (session.isLogging()) {
				//so yeah.... the default logger, in nukkitx is kind of lame, and in our case trace isn't enabled so we will just do this for now
				log.info("Inbound {}: {}", session.getAddress(), packet.toString().substring(0, Math.min(packet.toString().length(), 200)));
			}

			this.packetTranslatorManager.translateData(packet, this.bedrockConnection, this.javaConnection);
			
			//packet.handle(session.getPacketHandler());
		}
	}
}
