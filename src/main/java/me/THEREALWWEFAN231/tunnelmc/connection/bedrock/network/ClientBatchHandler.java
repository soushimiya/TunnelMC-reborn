package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;

import java.util.Collection;

@Log4j2
public class ClientBatchHandler implements BatchHandler {
	public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
		for (BedrockPacket packet : packets) {
			if (session.isLogging()) {
				//so yeah.... the default logger, in nukkitx is kind of lame, and in our case trace isn't enabled so we will just do this for now
				log.info("Inbound {}: {}", session.getAddress(), packet.toString().substring(0, Math.min(packet.toString().length(), 200)));
			}
			
			TunnelMC.instance.packetTranslatorManager.translatePacket(packet);
			
			//packet.handle(session.getPacketHandler());
		}
	}
}
