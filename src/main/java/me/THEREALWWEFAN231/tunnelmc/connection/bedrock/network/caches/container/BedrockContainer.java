package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

import java.util.ArrayList;

public class BedrockContainer {

	protected int size;
	protected ArrayList<ItemData> items;
	protected int id;

	public BedrockContainer(int size, int id) {
		this.size = size;
		this.id = id;

		this.items = new ArrayList<ItemData>();
		for (int i = 0; i < size; i++) {
			this.items.add(ItemData.AIR);
		}
	}

	public void setItemBedrock(int slot, ItemData itemData) {
		this.items.set(slot, itemData);
	}

	public void setItemFromJavaSlot(int javaSlot, ItemData itemData) {
		this.items.set(javaSlot, itemData);
	}
	
	public int convertJavaSlotIdToBedrockSlotId(int javaSlotId) {
		return 0;
	}

	public ItemData getItemFromSlot(int slot) {
		return this.items.get(slot);
	}

	public int getSize() {
		return this.size;
	}

	public ArrayList<ItemData> getItems() {
		return this.items;
	}

	public int getId() {
		return this.id;
	}

}
