package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BedrockContainer {

	protected int size;
	protected Map<Integer, ItemData> items = new HashMap<>();
	protected int id;

	public BedrockContainer(int size, int id) {
		this.size = size;
		this.id = id;

		for (int i = 0; i < size; i++) {
			this.items.put(i, ItemData.AIR);
		}
	}

	public void setItemBedrock(int slot, ItemData itemData) {
		this.items.put(slot, itemData);
	}

	public void setItemFromJavaSlot(int javaSlot, ItemData itemData) {
		this.setItemBedrock(convertJavaSlotIdToBedrockSlotId(javaSlot), itemData);
	}
	
	public abstract int convertJavaSlotIdToBedrockSlotId(int javaSlotId);

	public ItemData getItemFromSlot(int slot) {
		return this.items.get(slot);
	}

	public int getSize() {
		return this.size;
	}

	public Collection<ItemData> getItems() {
		return this.items.values();
	}

	public int getId() {
		return this.id;
	}

}
