package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

@PacketIdentifier(UpdateSelectedSlotC2SPacket.class)
public class UpdateSelectedSlotC2STranslator extends PacketTranslator<UpdateSelectedSlotC2SPacket> {

	@Override
	public void translate(UpdateSelectedSlotC2SPacket packet, BedrockConnection bedrockConnection) {
		UpdateSelectedSlotC2STranslator.updateHotbarItem(packet.getSelectedSlot(), bedrockConnection);
	}
	
	public static void updateHotbarItem(int hotbarSlot, BedrockConnection bedrockConnection) { // TODO: not here
		if(hotbarSlot < 0 || hotbarSlot > 8) {
			System.out.println("Can not send an invalid hotbar slot");
			return;
		}
		
		long runtimeEntityId = TunnelMC.mc.player.getId();
		BedrockContainer container = bedrockConnection.containers.getPlayerInventory();
		
		ItemData item = container.getItemFromSlot(hotbarSlot);
		
		MobEquipmentPacket mobEquipmentPacket = new MobEquipmentPacket();
		mobEquipmentPacket.setRuntimeEntityId(runtimeEntityId);
		mobEquipmentPacket.setItem(item);
		mobEquipmentPacket.setInventorySlot(hotbarSlot);
		mobEquipmentPacket.setHotbarSlot(hotbarSlot);
		mobEquipmentPacket.setContainerId(container.getId());

		bedrockConnection.sendPacket(mobEquipmentPacket);
	}
}
