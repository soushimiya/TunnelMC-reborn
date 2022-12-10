package me.THEREALWWEFAN231.tunnelmc.connection;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;

public abstract class PacketTranslator<T> {

	public abstract void translate(T packet, BedrockConnection bedrockConnection);

	public boolean idleUntil() {
		return false;
	}
}
