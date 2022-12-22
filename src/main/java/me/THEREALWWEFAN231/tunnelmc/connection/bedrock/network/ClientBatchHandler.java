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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class ClientBatchHandler implements BatchHandler {
	private final BedrockConnection bedrockConnection;
	private final FakeJavaConnection javaConnection;
	private final PacketTranslatorManager<BedrockPacket> packetTranslatorManager;
	private final List<BedrockPacket> unexpectedPackets = new ArrayList<>();

	public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
		for (BedrockPacket packet : packets) {
			if (session.isLogging()) {
				log.info("Inbound {}: {}", session.getAddress(), packet.toString().substring(0, Math.min(packet.toString().length(), 200)));
			}

			if(!handlePacket(packet)) {
				this.unexpectedPackets.add(packet);
			}
		}

		this.unexpectedPackets.removeIf(this::handlePacket);
	}

	private boolean handlePacket(BedrockPacket packet) {
		for (Class<?> clazz : bedrockConnection.getExpectedPackets()) {
			if(!clazz.equals(packet.getClass()) && !bedrockConnection.isSpawned()) {
				continue;
			}

			this.packetTranslatorManager.translateData(packet, this.bedrockConnection, this.javaConnection);
			return true;
		}
		return false;
	}
}
