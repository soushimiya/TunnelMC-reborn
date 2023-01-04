package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.ItemStack;

public class PlayerOffhandContainer extends GenericContainer {
	private static final int SIZE = 1;

	public PlayerOffhandContainer() {
		super(PlayerOffhandContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void updateInventory() {
		ItemStack stack = ItemTranslator.itemDataToItemStack(this.getItemFromSlot(0));
		TunnelMC.mc.player.playerScreenHandler.getSlot(45).setStack(stack);
	}
}
