package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerInteractEntityC2SPacket;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@PacketIdentifier(PlayerInteractEntityC2SPacket.class)
public class PlayerInteractEntityC2STranslator extends PacketTranslator<PlayerInteractEntityC2SPacket>{

	@Override
	public void translate(PlayerInteractEntityC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		ItemData holdingItem = bedrockConnection.getWrappedContainers().getPlayerInventory()
				.getItemFromSlot(TunnelMC.mc.player.getInventory().selectedSlot);
		
		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		inventoryTransactionPacket.setTransactionType(TransactionType.ITEM_USE_ON_ENTITY);
		inventoryTransactionPacket.setActionType(1);
		inventoryTransactionPacket.setRuntimeEntityId(((IMixinPlayerInteractEntityC2SPacket) packet).getEntityId());
		inventoryTransactionPacket.setHotbarSlot(TunnelMC.mc.player.getInventory().selectedSlot);
		inventoryTransactionPacket.setItemInHand(holdingItem);
		inventoryTransactionPacket.setPlayerPosition(Vector3f.from(TunnelMC.mc.player.getPos().x, TunnelMC.mc.player.getPos().y + TunnelMC.mc.player.getEyeHeight(EntityPose.STANDING), TunnelMC.mc.player.getPos().z));
		inventoryTransactionPacket.setClickPosition(Vector3f.ZERO);
		
		bedrockConnection.sendPacket(inventoryTransactionPacket);
	}
}
