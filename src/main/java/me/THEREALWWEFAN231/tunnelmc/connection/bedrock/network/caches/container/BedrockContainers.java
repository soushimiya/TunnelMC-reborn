package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerArmorContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerContainerCursorContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerInventoryContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerOffhandContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BedrockContainers {
	
	public static final int PLAYER_INVENTORY_COTNAINER_ID = 0;
	public static final int PLAYER_OFFHAND_COTNAINER_ID = 119;
	public static final int PLAYER_ARMOR_COTNAINER_ID = 120;
	public static final int PLAYER_CONTAINER_CURSOR_COTNAINER_ID = 124;

	private final Map<Integer, BedrockContainer> containers = new HashMap<>();
	private int revision = 0;
	private int currentlyOpenContainerId;
	
	public BedrockContainers() {
		this.containers.put(BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID, new PlayerInventoryContainer());
		this.containers.put(BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID, new PlayerOffhandContainer());
		this.containers.put(BedrockContainers.PLAYER_ARMOR_COTNAINER_ID, new PlayerArmorContainer());
		this.containers.put(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID, new PlayerContainerCursorContainer());
	}

	public Map<Integer, BedrockContainer> getContainers() {
		return Collections.unmodifiableMap(this.containers);
	}

	public BedrockContainer getContainer(int id) {
		return this.containers.get(id);
	}

	public BedrockContainer getPlayerInventory() {
		return this.getContainer(BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID);
	}

	public BedrockContainer getPlayerOffhandContainer() {
		return this.getContainer(BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID);
	}

	public BedrockContainer getPlayerArmorContainer() {
		return this.getContainer(BedrockContainers.PLAYER_ARMOR_COTNAINER_ID);
	}

	public BedrockContainer getPlayerContainerCursorContainer() {
		return this.getContainer(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID);
	}

	public int getCurrentlyOpenContainerId() {
		return this.currentlyOpenContainerId;
	}

	public BedrockContainer getCurrentlyOpenContainer() {
		return this.getContainers().get(this.currentlyOpenContainerId);
	}

	public void setCurrentlyOpenContainer(int id, BedrockContainer currentlyOpenContainer) {
		if(currentlyOpenContainer != null) {
			if(!currentlyOpenContainer.isStatic()) {
				this.containers.put(id, currentlyOpenContainer);
			}

			this.currentlyOpenContainerId = id;
			return;
		}

		BedrockContainer container = this.getContainer(this.currentlyOpenContainerId);
		if(container != null) {
			if (!container.isStatic()) {
				this.containers.remove(id);
			}
		}

		this.currentlyOpenContainerId = -1;
	}

	public int nextRevision() {
		this.revision = this.revision + 1 & Short.MAX_VALUE;
		return this.revision;
	}
}
