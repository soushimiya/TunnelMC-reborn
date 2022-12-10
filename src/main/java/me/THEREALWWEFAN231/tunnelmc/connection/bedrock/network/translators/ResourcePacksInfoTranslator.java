package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.ClientCacheStatusPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

@PacketIdentifier(ResourcePacksInfoPacket.class)
public class ResourcePacksInfoTranslator extends PacketTranslator<ResourcePacksInfoPacket> {

	@Override
	public void translate(ResourcePacksInfoPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		bedrockConnection.sendPacketImmediately(new ClientCacheStatusPacket());

		ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
		resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);

		bedrockConnection.sendPacketImmediately(resourcePackClientResponsePacket);
	}
}
