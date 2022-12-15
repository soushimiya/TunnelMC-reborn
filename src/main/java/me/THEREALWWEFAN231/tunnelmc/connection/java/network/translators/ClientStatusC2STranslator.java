package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket.State;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;

@PacketIdentifier(ClientStatusC2SPacket.class)
public class ClientStatusC2STranslator extends PacketTranslator<ClientStatusC2SPacket> {

	@Override
	public void translate(ClientStatusC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		if (packet.getMode() == ClientStatusC2SPacket.Mode.PERFORM_RESPAWN) {
			RespawnPacket respawnPacket = new RespawnPacket();
			respawnPacket.setPosition(Vector3f.ZERO);
			respawnPacket.setState(State.CLIENT_READY);
			respawnPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

			bedrockConnection.sendPacket(respawnPacket);
		}
	}
}
