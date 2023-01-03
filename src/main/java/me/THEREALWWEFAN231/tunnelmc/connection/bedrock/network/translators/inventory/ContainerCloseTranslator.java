package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;

@Log4j2
@PacketIdentifier(ContainerClosePacket.class)
public class ContainerCloseTranslator extends PacketTranslator<ContainerClosePacket> {

	@Override
	public void translate(ContainerClosePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		CloseScreenS2CPacket closeScreenS2CPacket = new CloseScreenS2CPacket(packet.getId() & 0xff);
		javaConnection.processJavaPacket(closeScreenS2CPacket);

		bedrockConnection.getWrappedContainers().setCurrentlyOpenContainer(packet.getId(), null);
	}
}
