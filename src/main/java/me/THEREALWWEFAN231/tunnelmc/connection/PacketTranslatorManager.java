package me.THEREALWWEFAN231.tunnelmc.connection;

import com.nukkitx.api.event.Listener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PacketTranslatorManager<P> {
	private final Map<Class<P>, PacketTranslator<P>> packetTranslatorsByPacketClass = new HashMap<>();
	private final List<IdlePacket> idlePackets = new CopyOnWriteArrayList<>();

	public PacketTranslatorManager() {
		TunnelMC.instance.eventManager.registerListeners(this, this);
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

	public void translatePacket(P packet) {
		PacketTranslator<P> packetTranslator = this.packetTranslatorsByPacketClass.get(packet.getClass());
		if (packetTranslator == null) {
			//System.out.println("Could not find a packet translator for the packet: " + packet.getClass());
			return;
		}
		if (packetTranslator.idleUntil()) {
			this.idlePackets.add(new IdlePacket(packetTranslator, packet));
			return;
		}

		packetTranslator.translate(packet, BedrockConnectionAccessor.getCurrentConnection());
	}

	@Listener
	private void onEvent(PlayerTickEvent event) {
		for (int i = 0; i < this.idlePackets.size(); i++) {
			IdlePacket idlePacket = this.idlePackets.get(i);
			if (idlePacket.packetTranslator.idleUntil()) {
				continue;
			}

			idlePacket.getPacketTranslator().translate(idlePacket.getPacket(), BedrockConnectionAccessor.getCurrentConnection());
			this.idlePackets.remove(i);
			i--;
		}
	}

	@Getter
	@RequiredArgsConstructor
	private class IdlePacket {
		private final PacketTranslator<P> packetTranslator;
		private final P packet;
	}
}
