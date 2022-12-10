package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.util.collection.DefaultedList;

@PacketIdentifier(InventoryContentPacket.class)
public class InventoryContentTranslator extends PacketTranslator<InventoryContentPacket> {

	@Override
	public void translate(InventoryContentPacket packet, Client client) {
		if (TunnelMC.mc.player == null) {
			return;
		}

		int syncId = packet.getContainerId();
		int javaContainerSize = packet.getContents().size();

		BedrockContainer containerAffected = client.containers.getContainers().get(syncId);
		if (containerAffected == null) {
			containerAffected = client.containers.getCurrentlyOpenContainer();
		}

		switch (syncId) {
			case BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID -> {
				for (int i = 0; i < javaContainerSize; i++) {
					ItemData bedrockItemStack = packet.getContents().get(i);
					ItemStack translatedStack = ItemTranslator.itemDataToItemStack(bedrockItemStack);

					Integer javaSlotId = ScreenHandlerTranslatorManager.getJavaSlotFromBedrockContainer(TunnelMC.mc.player.currentScreenHandler, containerAffected, i);
					if (javaSlotId == null) {
						break;
					}

					containerAffected.setItemBedrock(i, bedrockItemStack);
					TunnelMC.mc.player.playerScreenHandler.getSlot(javaSlotId).setStack(translatedStack);
				}
			}
			case BedrockContainers.PLAYER_ARMOR_COTNAINER_ID -> {
				for (int i = 0; i < javaContainerSize; i++) {
					ItemData bedrockItemStack = packet.getContents().get(i);
					ItemStack translatedStack = ItemTranslator.itemDataToItemStack(bedrockItemStack);

					containerAffected.setItemBedrock(i, bedrockItemStack);
					TunnelMC.mc.player.playerScreenHandler.getSlot(5 + i).setStack(translatedStack);
				}
			}
			case BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID -> {
				ItemData bedrockItemStack = packet.getContents().get(0);
				ItemStack translatedStack = ItemTranslator.itemDataToItemStack(bedrockItemStack);

				containerAffected.setItemBedrock(0, bedrockItemStack);
				TunnelMC.mc.player.playerScreenHandler.getSlot(45).setStack(translatedStack);
			}
			default -> {
				DefaultedList<ItemStack> javaContents = DefaultedList.ofSize(packet.getContents().size(), ItemStack.EMPTY);
				for (int i = 0; i < javaContainerSize; i++) {
					ItemData bedrockItemStack = packet.getContents().get(i);
					ItemStack translatedStack = ItemTranslator.itemDataToItemStack(bedrockItemStack);

					javaContents.set(i, translatedStack);
					containerAffected.setItemBedrock(i, packet.getContents().get(i));
				}
				InventoryS2CPacket inventoryS2CPacket = new InventoryS2CPacket(syncId, client.nextRevision(), javaContents, ItemStack.EMPTY);
				client.javaConnection.processServerToClientPacket(inventoryS2CPacket);
			}
		}
	}
}