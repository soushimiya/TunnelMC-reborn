package me.THEREALWWEFAN231.tunnelmc.connection;

import com.nukkitx.api.event.Listener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PacketTranslatorManager<P> {
	private final Map<Class<P>, PacketTranslator<P>> packetTranslatorsByPacketClass = new HashMap<>();
	private final List<IdlePacket> idlePackets = new CopyOnWriteArrayList<>();

	public PacketTranslatorManager() {
		TunnelMC.getInstance().getEventManager().registerListeners(this, this);
	}

	@SuppressWarnings("unchecked")
	protected void addTranslator(PacketTranslator<?> translator) {
		if(!translator.getClass().isAnnotationPresent(PacketIdentifier.class)) {
			System.out.println("Skipping translator due to not having an annotation: " + translator.getClass().getSimpleName());
			return;
		}

		PacketIdentifier identifier = translator.getClass().getAnnotation(PacketIdentifier.class);
		this.packetTranslatorsByPacketClass.put((Class<P>) identifier.value(), (PacketTranslator<P>) translator);
	}

	public void translatePacket(P packet, BedrockConnection bedrockConnection, FakeJavaConnection connection) {
		PacketTranslator<P> packetTranslator = this.packetTranslatorsByPacketClass.get(packet.getClass());
		if (packetTranslator == null) {
			//System.out.println("Could not find a packet translator for the packet: " + packet.getClass());
			return;
		}
		if (!packetTranslator.idleUntil(packet, bedrockConnection, connection)) {
			this.idlePackets.add(new IdlePacket(bedrockConnection, connection, packetTranslator, packet));
			return;
		}

		try {
			packetTranslator.translate(packet, bedrockConnection, connection);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	@Listener
	public void onEvent(PlayerTickEvent event) {
		for (int i = 0; i < this.idlePackets.size(); i++) {
			IdlePacket idlePacket = this.idlePackets.get(i);
			if (!idlePacket.getPacketTranslator().idleUntil(idlePacket.getPacket(), idlePacket.getBedrockConnection(), idlePacket.getJavaConnection())) {
				continue;
			}

			idlePacket.getPacketTranslator().translate(idlePacket.getPacket(), idlePacket.getBedrockConnection(), idlePacket.getJavaConnection());
			this.idlePackets.remove(i);
			i--;
		}
	}

	@Getter
	@RequiredArgsConstructor
	private class IdlePacket {
		private final BedrockConnection bedrockConnection;
		private final FakeJavaConnection javaConnection;
		private final PacketTranslator<P> packetTranslator;
		private final P packet;
	}
}
