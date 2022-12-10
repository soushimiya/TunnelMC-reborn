package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.UpdateSelectedSlotC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

@PacketIdentifier(InventorySlotPacket.class)
public class InventorySlotTranslator extends PacketTranslator<InventorySlotPacket> {

	@Override
	public void translate(InventorySlotPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int syncId = packet.getContainerId();
		BedrockContainer containerToChange = bedrockConnection.getWrappedContainers().getContainers().get(syncId);
		if(containerToChange == null) {//TODO: create some sort of "temp" container, we use to do this, but for testing purposes this does for now
			System.out.println("Couldn't find container with id " + syncId);
			return;
		}
		
		int javaInventorySlot = packet.getSlot();
		int packetSlot = packet.getSlot();
		ItemStack stack = ItemTranslator.itemDataToItemStack(packet.getItem());

		if (syncId == 0) {//TODO: change this/find a better way
			if (javaInventorySlot < 9) {
				javaInventorySlot += 36;
			}
		}

		ScreenHandlerSlotUpdateS2CPacket handlerSlotUpdateS2CPacket = new ScreenHandlerSlotUpdateS2CPacket(syncId, bedrockConnection.getWrappedContainers().nextRevision(), javaInventorySlot, stack);
		javaConnection.processJavaPacket(handlerSlotUpdateS2CPacket);

		containerToChange.setItemBedrock(packet.getSlot(), packet.getItem());

		//not fully sure if "vanilla" bedrock does it like this, but for example, we could be at slot 0, and get a new item in that slot, and we are still holding nothing, so we have to update our held item, this is stupid though, it should be server side
		if (packetSlot == TunnelMC.mc.player.getInventory().selectedSlot) {
			UpdateSelectedSlotC2STranslator.updateHotbarItem(packetSlot, bedrockConnection);
		}
	}

	@Override
	public boolean idleUntil() {
		return TunnelMC.mc.player == null;
	}
}
