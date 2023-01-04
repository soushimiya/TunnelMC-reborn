package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;

public class PlayerContainerCursorContainer extends GenericContainer {
	private static final int SIZE = 1;

	public PlayerContainerCursorContainer() {
		super(PlayerContainerCursorContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void updateInventory() {
		TunnelMC.mc.player.currentScreenHandler.setCursorStack(ItemTranslator.itemDataToItemStack(this.getItemFromSlot(0)));
	}
}
