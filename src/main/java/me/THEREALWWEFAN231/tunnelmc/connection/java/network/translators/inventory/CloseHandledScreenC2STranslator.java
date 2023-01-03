package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@PacketIdentifier(CloseHandledScreenC2SPacket.class)
public class CloseHandledScreenC2STranslator extends PacketTranslator<CloseHandledScreenC2SPacket> {

	@Override
	public void translate(CloseHandledScreenC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int id = packet.getSyncId();
		if (id == 0) {
			id = bedrockConnection.getWrappedContainers().getCurrentlyOpenContainerId();
		}

		bedrockConnection.getWrappedContainers().setCurrentlyOpenContainer(id, null);
		ContainerClosePacket containerClosePacket = new ContainerClosePacket();
		containerClosePacket.setId((byte) id);
		
		bedrockConnection.sendPacket(containerClosePacket);
	}
}
