package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.ItemStack;

public class PlayerInventoryContainer extends GenericContainer {
	private static final int SIZE = 36;

	public PlayerInventoryContainer() {
		super(PlayerInventoryContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public int getJavaSlotId(int bedrockSlotId) {
		if(bedrockSlotId < 9) { // for the hotbar
			return bedrockSlotId + 36;
		}

		return super.getJavaSlotId(bedrockSlotId);
	}

	@Override
	public int getBedrockSlotId(int javaSlotId) {
		if(javaSlotId >= 36 && javaSlotId < 45) { // for the hotbar
			return javaSlotId - 36;
		}

		return super.getBedrockSlotId(javaSlotId);
	}

	@Override
	public void updateInventory() {
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack stack = ItemTranslator.itemDataToItemStack(this.getItemFromSlot(i));

			TunnelMC.mc.player.playerScreenHandler.getSlot(this.getJavaSlotId(i)).setStack(stack);
		}
	}
}
