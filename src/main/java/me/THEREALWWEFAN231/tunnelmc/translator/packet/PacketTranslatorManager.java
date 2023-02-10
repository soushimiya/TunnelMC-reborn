package me.THEREALWWEFAN231.tunnelmc.translator.packet;

import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.TranslatorManager;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public abstract class PacketTranslatorManager<P> extends TranslatorManager<PacketTranslator<?>, P> {
	private final Map<Class<P>, PacketTranslator<P>> packetTranslatorsByPacketClass = new HashMap<>();

	public PacketTranslatorManager(BedrockConnection bedrockConnection) {
		TunnelMC.getInstance().getEventManager().registerListeners(bedrockConnection, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTranslator(PacketTranslator<?> translator) {
		if(!translator.getClass().isAnnotationPresent(PacketIdentifier.class)) {
			log.warn("Skipping translator due to not having an annotation: " + translator.getClass().getSimpleName());
			return;
		}

		PacketIdentifier identifier = translator.getClass().getAnnotation(PacketIdentifier.class);
		this.packetTranslatorsByPacketClass.put((Class<P>) identifier.value(), (PacketTranslator<P>) translator);
	}

	@Override
	public void translateData(P packet, BedrockConnection bedrockConnection, FakeJavaConnection connection) {
		PacketTranslator<P> packetTranslator = this.packetTranslatorsByPacketClass.get(packet.getClass());
		if (packetTranslator == null) {
			log.debug("Could not find a packet translator for the packet: " + packet.getClass());
			return;
		}

		try {
			packetTranslator.translateType(packet, bedrockConnection, connection);
		} catch (Throwable throwable) {
			log.catching(throwable);
		}
	}
}
