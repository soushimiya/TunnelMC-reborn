package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

@PacketIdentifier(ResourcePackStackPacket.class)
public class ResourcePackStackTranslator extends PacketTranslator<ResourcePackStackPacket> {

	@Override
	public void translate(ResourcePackStackPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
		resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);

		bedrockConnection.sendPacketImmediately(resourcePackClientResponsePacket);
	}
}
