package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;

@PacketIdentifier(ContainerClosePacket.class)
public class ContainerCloseTranslator extends PacketTranslator<ContainerClosePacket> {

	@Override
	public void translate(ContainerClosePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if(!packet.isUnknownBool0()) { // Only act when it's true, because the server wants to close the container
			return;
		}

		bedrockConnection.getWrappedContainers().setCurrentlyOpenContainer(packet.getId(), null);

		CloseScreenS2CPacket closeScreenS2CPacket = new CloseScreenS2CPacket(packet.getId());
		javaConnection.processJavaPacket(closeScreenS2CPacket);
	}
}
