package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BedrockContainer {
	protected int size;
	protected Map<Integer, ItemData> items = new HashMap<>();

	public BedrockContainer(int size) {
		this.size = size;

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
	
	protected abstract int convertJavaSlotIdToBedrockSlotId(int javaSlotId);

	public ItemData getItemFromSlot(int slot) {
		return this.items.get(slot);
	}

	public int getSize() {
		return this.size;
	}

	public List<ItemData> getItems() {
		List<ItemData> itemDataList = DefaultedList.ofSize(this.getSize());
		for(Map.Entry<Integer, ItemData> entry : this.items.entrySet()) {
			itemDataList.add(entry.getKey(), entry.getValue());
		}
		return itemDataList;
	}

	public abstract boolean isStatic();

	public abstract void updateInventory();
}
