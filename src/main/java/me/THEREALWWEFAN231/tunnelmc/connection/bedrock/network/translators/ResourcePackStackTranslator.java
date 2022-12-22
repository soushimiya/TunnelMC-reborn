package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@PacketIdentifier(ResourcePackStackPacket.class)
public class ResourcePackStackTranslator extends PacketTranslator<ResourcePackStackPacket> {

	@Override
	public void translate(ResourcePackStackPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
		resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);

		bedrockConnection.expect(StartGamePacket.class);
		bedrockConnection.sendPacketImmediately(resourcePackClientResponsePacket);
	}
}
