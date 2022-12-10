package me.THEREALWWEFAN231.tunnelmc.connection;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

public abstract class PacketTranslator<T> {

	public abstract void translate(T packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection);

	public boolean idleUntil() {
		return false;
	}
}
