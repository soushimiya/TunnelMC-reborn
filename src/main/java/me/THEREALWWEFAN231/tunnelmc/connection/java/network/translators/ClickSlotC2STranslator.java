package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.api.event.Listener;
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource.Flag;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.slot.DropSlotEvent;
import me.THEREALWWEFAN231.tunnelmc.events.slot.PlaceStackOnEmptySlotEvent;
import me.THEREALWWEFAN231.tunnelmc.events.slot.TakeSlotEvent;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.ItemDataUtils;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;

public class ClickSlotC2STranslator extends PacketTranslator<ClickSlotC2SPacket> {
	// TODO: Re-review this code and clean it up later on, seems incorrect.
	// TODO: should this still be a packet translator?

	public ClickSlotC2STranslator() {
		TunnelMC.getInstance().getEventManager().registerListeners(this, this);
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
		bedrockSlotId = JavaContainerFinder.getBedrockSlotFromJavaContainer(screenHandler, clickedSlotId, containerForClickedSlot);
		
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
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockContainer cursorContainer = BedrockConnectionAccessor.getCurrentConnection().getWrappedContainers().getPlayerContainerCursorContainer();
		BedrockContainer containerForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerFromJava(event.getScreenHandler(), clickedSlotId);
		if (containerForClickedSlot == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId, containerForClickedSlot);
		if (bedrockSlotId == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		ItemData cursorItemData = cursorContainer.getItemFromSlot(0);

		// Decrease if the user right-clicked a slot, change to air if they left-clicked a slot.
		ItemData decreasedCursorStack = ItemDataUtils.copyWithCount(cursorItemData, cursorItemData.getCount() - event.getCount());
		if (decreasedCursorStack.getCount() == 0) {
			decreasedCursorStack = ItemData.AIR;
		}

		InventoryActionData decreaseCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(cursorContainer.getId()), 0, cursorItemData, decreasedCursorStack);
		inventoryTransactionPacket.getActions().add(decreaseCursorStack);
		cursorContainer.setItemBedrock(0, decreasedCursorStack);

		ItemData clickedSlotNewItemData = ItemDataUtils.copyWithCount(cursorItemData, event.getCount());

		// Changes it to the cursor slot stack.
		InventoryActionData incrementClickedSlotWithCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerForClickedSlot.getId()), bedrockSlotId, ItemData.AIR, clickedSlotNewItemData);
		inventoryTransactionPacket.getActions().add(incrementClickedSlotWithCursorStack);
		containerForClickedSlot.setItemBedrock(bedrockSlotId, clickedSlotNewItemData);

		BedrockConnectionAccessor.getCurrentConnection().sendPacket(inventoryTransactionPacket);
	}

	@Listener
	public void onEvent(TakeSlotEvent event) {
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockContainer cursorContainer = BedrockConnectionAccessor.getCurrentConnection().getWrappedContainers().getPlayerContainerCursorContainer();
		BedrockContainer containerForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerFromJava(event.getScreenHandler(), clickedSlotId);
		if (containerForClickedSlot == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId, containerForClickedSlot);
		if (bedrockSlotId == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		ItemData clickedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);

		InventoryActionData changeClickedStackToAir = new InventoryActionData(InventorySource.fromContainerWindowId(containerForClickedSlot.getId()), bedrockSlotId, clickedSlotItemData, ItemData.AIR);
		inventoryTransactionPacket.getActions().add(changeClickedStackToAir);
		containerForClickedSlot.setItemBedrock(bedrockSlotId, ItemData.AIR);

		InventoryActionData moveClickedStackToCursorContainer = new InventoryActionData(InventorySource.fromContainerWindowId(cursorContainer.getId()), 0, ItemData.AIR, clickedSlotItemData);
		inventoryTransactionPacket.getActions().add(moveClickedStackToCursorContainer);
		cursorContainer.setItemBedrock(0, clickedSlotItemData);

		BedrockConnectionAccessor.getCurrentConnection().sendPacket(inventoryTransactionPacket);
	}

	@Listener
	public void onEvent(DropSlotEvent event) {
		int clickedSlotId = event.getSlotIndex();

		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockContainer containerForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerFromJava(event.getScreenHandler(), clickedSlotId);

		if (containerForClickedSlot == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}

		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId, containerForClickedSlot);
		if (bedrockSlotId == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}

		ItemData droppedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
		ItemData afterDropSlotItemData;
		if (event.getButton() == 0) {//1 item is dropped
			afterDropSlotItemData = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedSlotItemData.getCount() - 1);
		} else {//all items
			afterDropSlotItemData = ItemData.AIR;
		}
		InventoryActionData decreaseClickedStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerForClickedSlot.getId()), bedrockSlotId, droppedSlotItemData, afterDropSlotItemData);
		inventoryTransactionPacket.getActions().add(decreaseClickedStack);

		int droppedItemCount = event.getButton() == 0 ? 1 : droppedSlotItemData.getCount();
		ItemData itemDroppedInTheWorld = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedItemCount);
		InventoryActionData dropItemInWorld = new InventoryActionData(InventorySource.fromWorldInteraction(Flag.DROP_ITEM), 0, ItemData.AIR, itemDroppedInTheWorld);
		inventoryTransactionPacket.getActions().add(dropItemInWorld);
		BedrockConnectionAccessor.getCurrentConnection().sendPacket(inventoryTransactionPacket);

		containerForClickedSlot.setItemBedrock(bedrockSlotId, afterDropSlotItemData);
	}
}
