package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.api.event.Listener;
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource.Flag;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.slot.DropSlotEvent;
import me.THEREALWWEFAN231.tunnelmc.events.slot.PlaceStackOnEmptySlotEvent;
import me.THEREALWWEFAN231.tunnelmc.events.slot.TakeSlotEvent;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.ItemDataUtils;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;

@Log4j2
public class ClickSlotC2STranslator extends PacketTranslator<ClickSlotC2SPacket> {
	// TODO: Re-review this code and clean it up later on, seems incorrect.
	// TODO: should this still be a packet translator?

	public ClickSlotC2STranslator(BedrockConnection bedrockConnection) {
		TunnelMC.getInstance().getEventManager().registerListeners(bedrockConnection, this);
	}

	@Override
	public void translate(ClickSlotC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
	}

	public void onCursorStackAddToStack(ScreenHandler screenHandler, int clickedSlotId) {//for example the user has 64 oak planks in the cursor, and they right-click a slot with oak planks(not an empty slot)
		/*InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getEntityId());
		
		BedrockContainer containerForClickedSlot = JavaContainerFinder.getContainerFromJava(screenHandler, clickedSlotId);
		
		if (containerForClickedSlot == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		
		int bedrockSlotId = containerForClickedSlot.convertJavaSlotIdToBedrockSlotId(clickedSlotId);
		bedrockSlotId = JavaContainerFinder.getBedrockSlotId(screenHandler, clickedSlotId, containerForClickedSlot);
		
		ItemData droppedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
		ItemData afterDropSlotItemData = null;
		if (clickData == 0) {//1 item is dropped
			afterDropSlotItemData = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedSlotItemData.getCount() - 1);
		} else {//all items
			afterDropSlotItemData = ItemData.AIR;
		}
		
		{
			InventoryActionData decreaseClickedStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerForClickedSlot.getId()), bedrockSlotId, droppedSlotItemData, afterDropSlotItemData);
			inventoryTransactionPacket.getActions().add(decreaseClickedStack);
		}
		
		{
			int droppedItemCount = clickData == 0 ? 1 : droppedSlotItemData.getCount();
			ItemData itemDroppedInTheWorld = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedItemCount);
		
			InventoryActionData dropItemInWorld = new InventoryActionData(InventorySource.fromWorldInteraction(Flag.DROP_ITEM), 0, ItemData.AIR, itemDroppedInTheWorld);
			inventoryTransactionPacket.getActions().add(dropItemInWorld);
		}
		
		containerForClickedSlot.setItemBedrock(bedrockSlotId, afterDropSlotItemData);
		
		client.sendPacket(inventoryTransactionPacket);*/
	}

	@Listener
	public void onEvent(PlaceStackOnEmptySlotEvent event) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockContainer cursorContainer = bedrockConnection.getWrappedContainers().getPlayerContainerCursorContainer();
		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
		if (containerIdForClickedSlot == null) {
			return;
		}
		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);
		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
		if (bedrockSlotId == null) {
			return;
		}
		ItemData cursorItemData = cursorContainer.getItemFromSlot(0);

		// Decrease if the user right-clicked a slot, change to air if they left-clicked a slot.
		ItemData decreasedCursorStack = ItemDataUtils.copyWithCount(cursorItemData, cursorItemData.getCount() - event.getCount());
		if (decreasedCursorStack.getCount() == 0) {
			decreasedCursorStack = ItemData.AIR;
		}

		InventoryActionData decreaseCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID), 0, cursorItemData, decreasedCursorStack);
		inventoryTransactionPacket.getActions().add(decreaseCursorStack);
		cursorContainer.setItemBedrock(0, decreasedCursorStack);

		ItemData clickedSlotNewItemData = ItemDataUtils.copyWithCount(cursorItemData, event.getCount());

		// Changes it to the cursor slot stack.
		InventoryActionData incrementClickedSlotWithCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, ItemData.AIR, clickedSlotNewItemData);
		inventoryTransactionPacket.getActions().add(incrementClickedSlotWithCursorStack);
		containerForClickedSlot.setItemBedrock(bedrockSlotId, clickedSlotNewItemData);

		bedrockConnection.sendPacket(inventoryTransactionPacket);
	}

	@Listener
	public void onEvent(TakeSlotEvent event) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockContainer cursorContainer = bedrockConnection.getWrappedContainers().getPlayerContainerCursorContainer();
		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
		if (containerIdForClickedSlot == null) {
			return;
		}
		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);
		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
		if (bedrockSlotId == null) {
			return;
		}
		ItemData clickedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);

		InventoryActionData changeClickedStackToAir = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, clickedSlotItemData, ItemData.AIR);
		inventoryTransactionPacket.getActions().add(changeClickedStackToAir);
		containerForClickedSlot.setItemBedrock(bedrockSlotId, ItemData.AIR);

		InventoryActionData moveClickedStackToCursorContainer = new InventoryActionData(InventorySource.fromContainerWindowId(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID), 0, ItemData.AIR, clickedSlotItemData);
		inventoryTransactionPacket.getActions().add(moveClickedStackToCursorContainer);
		cursorContainer.setItemBedrock(0, clickedSlotItemData);

		bedrockConnection.sendPacket(inventoryTransactionPacket);
	}

	@Listener
	public void onEvent(DropSlotEvent event) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
		if (containerIdForClickedSlot == null) {
			return;
		}
		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);

		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
		if (bedrockSlotId == null) {
			return;
		}

		ItemData droppedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
		ItemData afterDropSlotItemData;
		if (event.getButton() == 0) {//1 item is dropped
			afterDropSlotItemData = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedSlotItemData.getCount() - 1);
		} else {//all items
			afterDropSlotItemData = ItemData.AIR;
		}
		InventoryActionData decreaseClickedStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, droppedSlotItemData, afterDropSlotItemData);
		inventoryTransactionPacket.getActions().add(decreaseClickedStack);

		int droppedItemCount = event.getButton() == 0 ? 1 : droppedSlotItemData.getCount();
		ItemData itemDroppedInTheWorld = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedItemCount);
		InventoryActionData dropItemInWorld = new InventoryActionData(InventorySource.fromWorldInteraction(Flag.DROP_ITEM), 0, ItemData.AIR, itemDroppedInTheWorld);
		inventoryTransactionPacket.getActions().add(dropItemInWorld);
		bedrockConnection.sendPacket(inventoryTransactionPacket);

		containerForClickedSlot.setItemBedrock(bedrockSlotId, afterDropSlotItemData);
	}
}
