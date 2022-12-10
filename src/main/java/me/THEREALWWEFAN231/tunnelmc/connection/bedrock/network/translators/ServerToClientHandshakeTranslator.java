package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

import javax.crypto.SecretKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

@PacketIdentifier(ServerToClientHandshakePacket.class)
public class ServerToClientHandshakeTranslator extends PacketTranslator<ServerToClientHandshakePacket> {

	@Override
	public void translate(ServerToClientHandshakePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		// Thanks to ProxyPass for this portion of the code.
		try {
			String[] jwtSplit = packet.getJwt().split("\\.");
			String header = new String(Base64.getDecoder().decode(jwtSplit[0]));
			JsonObject headerObject = JsonParser.parseString(header).getAsJsonObject();
			
			String payload = new String(Base64.getDecoder().decode(jwtSplit[1]));
			JsonObject payloadObject = JsonParser.parseString(payload).getAsJsonObject();
			
			ECPublicKey serverKey = EncryptionUtils.generateKey(headerObject.get("x5u").getAsString());
			SecretKey key = EncryptionUtils.getSecretKey(bedrockConnection.getChainData().privateKey(), serverKey, Base64.getDecoder().decode(payloadObject.get("salt").getAsString()));

			bedrockConnection.enableEncryption(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
		bedrockConnection.sendPacketImmediately(clientToServerHandshake);
	}
}
