package me.THEREALWWEFAN231.tunnelmc.connection.java;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.JavaPacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.utils.NOOPTelemetrySender;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class FakeJavaConnection {
	private final BedrockConnection bedrockConnection;
	@Getter
	private final ClientPlayNetworkHandler clientPlayNetworkHandler;
	private final PacketTranslatorManager<Packet<?>> packetTranslatorManager;

	public FakeJavaConnection(BedrockConnection bedrockConnection) {
		this.bedrockConnection = bedrockConnection;
		ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		this.clientPlayNetworkHandler = new ClientPlayNetworkHandler(TunnelMC.mc, null, clientConnection, new GameProfile(bedrockConnection.getAuthData().identity(), bedrockConnection.getAuthData().displayName()), NOOPTelemetrySender.INSTANCE);
		this.packetTranslatorManager = new JavaPacketTranslatorManager();
	}

	public void translatePacket(Packet<?> packet) {
		this.packetTranslatorManager.translatePacket(packet, bedrockConnection, this);
	}

	public void processJavaPacket(Packet<ClientPlayPacketListener> packet) {
		//this is what minecraft does, ClientConnection.channelRead0()V
		try {
			packet.apply(this.clientPlayNetworkHandler);
		} catch (OffThreadException ignored) {}
	}
}
