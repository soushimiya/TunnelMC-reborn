package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerArmorContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerContainerCursorContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerInventoryContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerOffhandContainer;

import java.util.HashMap;
import java.util.Map;

public class BedrockContainers {
	
	public static final int PLAYER_INVENTORY_COTNAINER_ID = 0;
	public static final int PLAYER_OFFHAND_COTNAINER_ID = 119;
	public static final int PLAYER_ARMOR_COTNAINER_ID = 120;
	public static final int PLAYER_CONTAINER_CURSOR_COTNAINER_ID = 124;

	private int revision = 0;
	public byte openContainerId = 0;
	private BedrockContainer currentlyOpenContainer;
	
	private final Map<Integer, BedrockContainer> containers;
	
	private final PlayerInventoryContainer playerInventory;
	private final PlayerOffhandContainer playerOffhandContainer;
	private final PlayerArmorContainer playerArmorContainer;
	private final PlayerContainerCursorContainer playerContainerCursorContainer;
	
	public BedrockContainers() {
		this.containers = new HashMap<>();
		
		this.containers.put(BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID, this.playerInventory = new PlayerInventoryContainer());
		this.containers.put(BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID, this.playerOffhandContainer = new PlayerOffhandContainer());
		this.containers.put(BedrockContainers.PLAYER_ARMOR_COTNAINER_ID, this.playerArmorContainer = new PlayerArmorContainer());
		this.containers.put(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID, this.playerContainerCursorContainer = new PlayerContainerCursorContainer());
	}
	
	public Map<Integer, BedrockContainer> getContainers() {
		return this.containers;
	}
	
	public PlayerInventoryContainer getPlayerInventory() {
		return this.playerInventory;
	}

	public PlayerOffhandContainer getPlayerOffhandContainer() {
		return this.playerOffhandContainer;
	}

	public PlayerArmorContainer getPlayerArmorContainer() {
		return this.playerArmorContainer;
	}

	public PlayerContainerCursorContainer getPlayerContainerCursorContainer() {
		return this.playerContainerCursorContainer;
	}

	public BedrockContainer getCurrentlyOpenContainer() {
		return this.currentlyOpenContainer;
	}

	public void setCurrentlyOpenContainer(BedrockContainer currentlyOpenContainer) {
		this.currentlyOpenContainer = currentlyOpenContainer;
	}

	public int nextRevision() {
		this.revision = this.revision + 1 & Short.MAX_VALUE;
		return this.revision;
	}
}
