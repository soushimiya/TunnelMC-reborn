package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.UpdateSelectedSlotC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@Log4j2
@PacketIdentifier(InventorySlotPacket.class)
public class InventorySlotTranslator extends PacketTranslator<InventorySlotPacket> {

	@Override
	public void translate(InventorySlotPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int syncId = packet.getContainerId();

		BedrockContainer containerAffected = bedrockConnection.getWrappedContainers().getContainers().get(syncId);
		if (containerAffected == null) {
			containerAffected = bedrockConnection.getWrappedContainers().getCurrentlyOpenContainer();
		}

		containerAffected.setItemBedrock(packet.getSlot(), packet.getItem());
		TunnelMC.mc.executeSync(() -> {
			if (packet.getSlot() == TunnelMC.mc.player.getInventory().selectedSlot) {
				UpdateSelectedSlotC2STranslator.updateHotbarItem(packet.getSlot(), bedrockConnection);
			}
		});
		TunnelMC.mc.executeSync(containerAffected::updateInventory);
	}
}
