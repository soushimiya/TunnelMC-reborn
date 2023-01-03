package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

public class PlayerArmorContainer extends GenericContainer {
	private static final int SIZE = 4;

	public PlayerArmorContainer() {
		super(PlayerArmorContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
