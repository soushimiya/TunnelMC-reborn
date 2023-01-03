package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

public class PlayerContainerCursorContainer extends GenericContainer {
	private static final int SIZE = 1;

	public PlayerContainerCursorContainer() {
		super(PlayerContainerCursorContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
