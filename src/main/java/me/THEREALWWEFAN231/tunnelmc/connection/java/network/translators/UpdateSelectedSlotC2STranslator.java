package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

@Log4j2
@PacketIdentifier(UpdateSelectedSlotC2SPacket.class)
public class UpdateSelectedSlotC2STranslator extends PacketTranslator<UpdateSelectedSlotC2SPacket> {

	@Override
	public void translate(UpdateSelectedSlotC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		UpdateSelectedSlotC2STranslator.updateHotbarItem(packet.getSelectedSlot(), bedrockConnection);
	}
	
	public static void updateHotbarItem(int hotbarSlot, BedrockConnection bedrockConnection) { // TODO: not here
		if(hotbarSlot < 0 || hotbarSlot > 8) {
			log.error("Can not send an invalid hotbar slot");
			return;
		}
		
		long runtimeEntityId = TunnelMC.mc.player.getId();
		BedrockContainer container = bedrockConnection.getWrappedContainers().getPlayerInventory();
		
		ItemData item = container.getItemFromSlot(hotbarSlot);
		
		MobEquipmentPacket mobEquipmentPacket = new MobEquipmentPacket();
		mobEquipmentPacket.setRuntimeEntityId(runtimeEntityId);
		mobEquipmentPacket.setItem(item);
		mobEquipmentPacket.setInventorySlot(hotbarSlot);
		mobEquipmentPacket.setHotbarSlot(hotbarSlot);
		mobEquipmentPacket.setContainerId(BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID);

		bedrockConnection.sendPacket(mobEquipmentPacket);
	}
}
