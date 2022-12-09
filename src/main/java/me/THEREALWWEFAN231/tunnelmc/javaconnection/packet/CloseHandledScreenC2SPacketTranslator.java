package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet;

import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class CloseHandledScreenC2SPacketTranslator extends PacketTranslator<CloseHandledScreenC2SPacket> {

	@Override
	public void translate(CloseHandledScreenC2SPacket packet) {
		byte id = (byte) packet.getSyncId();
		if (id == 0) {
			// The main inventory being closed does not send a container close packet.
			// Sending this on PocketMine servers also crashes the client.
			return;
		}
		ContainerClosePacket containerClosePacket = new ContainerClosePacket();
		containerClosePacket.setId(id);
		
		Client.instance.sendPacket(containerClosePacket);
	}

	@Override
	public Class<CloseHandledScreenC2SPacket> getPacketClass() {
		return CloseHandledScreenC2SPacket.class;
	}
}
