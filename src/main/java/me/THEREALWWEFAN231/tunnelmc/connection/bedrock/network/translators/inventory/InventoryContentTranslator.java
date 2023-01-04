package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@PacketIdentifier(InventoryContentPacket.class)
public class InventoryContentTranslator extends PacketTranslator<InventoryContentPacket> {

	@Override
	public void translate(InventoryContentPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int syncId = packet.getContainerId();

		BedrockContainer containerAffected = bedrockConnection.getWrappedContainers().getContainers().get(syncId);
		if (containerAffected == null) {
			containerAffected = bedrockConnection.getWrappedContainers().getCurrentlyOpenContainer();
		}

		for (int slot = 0; slot < packet.getContents().size(); slot++) {
			containerAffected.setItemBedrock(slot, packet.getContents().get(slot));
		}
		if (!TunnelMC.mc.isOnThread()) {
			TunnelMC.mc.executeSync(containerAffected::updateInventory);
			return;
		}
		containerAffected.updateInventory();
	}

	@Override
	public boolean idleUntil(InventoryContentPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		return TunnelMC.mc.player != null;
	}
}