package me.THEREALWWEFAN231.tunnelmc.connection;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;

public abstract class PacketTranslator<T> {

	public abstract void translate(T packet, Client client);

	public boolean idleUntil() {
		return false;
	}
}
