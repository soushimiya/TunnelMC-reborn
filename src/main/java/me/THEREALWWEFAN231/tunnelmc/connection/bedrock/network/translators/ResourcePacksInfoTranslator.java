package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@PacketIdentifier(ResourcePacksInfoPacket.class)
public class ResourcePacksInfoTranslator extends PacketTranslator<ResourcePacksInfoPacket> {

	@Override
	public void translate(ResourcePacksInfoPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
		resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);

		bedrockConnection.expect(ResourcePackStackPacket.class);
		bedrockConnection.sendPacket(resourcePackClientResponsePacket);
	}
}
