package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;

public class PlayerArmorContainer extends GenericContainer {
	
	public static final int SIZE = 4;

	public PlayerArmorContainer() {
		super(PlayerArmorContainer.SIZE, BedrockContainers.PLAYER_ARMOR_COTNAINER_ID);
	}

}
