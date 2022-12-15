package me.THEREALWWEFAN231.tunnelmc.translator.packet;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.VoidTranslator;

public abstract class PacketTranslator<T> implements VoidTranslator<T> {
	protected BedrockConnection bedrockConnection;
	protected FakeJavaConnection javaConnection;

	public boolean idleUntil(T packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		return true;
	}
}
