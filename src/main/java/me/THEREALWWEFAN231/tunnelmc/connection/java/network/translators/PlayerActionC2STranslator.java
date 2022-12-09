package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.api.event.Listener;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.events.EventPlayerTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

@PacketIdentifier(PlayerActionC2SPacket.class)
public class PlayerActionC2STranslator extends PacketTranslator<PlayerActionC2SPacket> {
	private Direction lastDirection;
	private Vector3i lastBlockPosition;

	@Override
	public void translate(PlayerActionC2SPacket packet, Client client) {
		if (TunnelMC.mc.world == null || TunnelMC.mc.player == null || MinecraftClient.getInstance().interactionManager == null) {
			return;
		}
		int runtimeId = TunnelMC.mc.player.getId();

		Vector3i blockPosition = Vector3i.from(packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());
		switch (packet.getAction()) {
			case START_DESTROY_BLOCK: {
				this.lastDirection = packet.getDirection();
				this.lastBlockPosition = blockPosition;

				PlayerActionPacket playerActionPacket = new PlayerActionPacket();
				playerActionPacket.setRuntimeEntityId(runtimeId);
				playerActionPacket.setAction(PlayerActionType.START_BREAK);
				playerActionPacket.setBlockPosition(blockPosition);
				playerActionPacket.setFace(packet.getDirection().ordinal());

				client.sendPacket(playerActionPacket);

				TunnelMC.instance.eventManager.registerListeners(this, this);

				// For some reason, blocks with a hardness of 0 don't have the stop action sent.
				// If you're in creative, the same issue occurs.
				float hardness = TunnelMC.mc.world.getBlockState(packet.getPos()).getHardness(TunnelMC.mc.world, packet.getPos());
				if (MinecraftClient.getInstance().interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
					if (hardness != 0) {
						break;
					}
				}
			}
			case STOP_DESTROY_BLOCK: {
				PlayerActionPacket playerActionPacket = new PlayerActionPacket();
				playerActionPacket.setRuntimeEntityId(runtimeId);
				playerActionPacket.setAction(PlayerActionType.STOP_BREAK);
				playerActionPacket.setBlockPosition(blockPosition);
				playerActionPacket.setFace(packet.getDirection().ordinal());

				client.sendPacket(playerActionPacket);

				if (MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
					PlayerActionPacket creativePacket = new PlayerActionPacket();
					creativePacket.setRuntimeEntityId(runtimeId);
					creativePacket.setAction(PlayerActionType.DIMENSION_CHANGE_REQUEST_OR_CREATIVE_DESTROY_BLOCK);
					creativePacket.setBlockPosition(blockPosition);
					playerActionPacket.setFace(packet.getDirection().ordinal());

					client.sendPacket(playerActionPacket);
				}

				this.lastDirection = null;
				this.lastBlockPosition = null;
				TunnelMC.instance.eventManager.deregisterListener(this);

				InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
				inventoryTransactionPacket.setTransactionType(TransactionType.ITEM_USE);
				inventoryTransactionPacket.setActionType(2);
				inventoryTransactionPacket.setBlockPosition(blockPosition);
				inventoryTransactionPacket.setBlockFace(packet.getDirection().ordinal());
				inventoryTransactionPacket.setHotbarSlot(TunnelMC.mc.player.getInventory().selectedSlot);
				inventoryTransactionPacket.setItemInHand(client.containers.getPlayerInventory().getItemFromSlot(TunnelMC.mc.player.getInventory().selectedSlot));
				inventoryTransactionPacket.setPlayerPosition(Vector3f.from(TunnelMC.mc.player.getPos().x, TunnelMC.mc.player.getPos().y, TunnelMC.mc.player.getPos().z));
				inventoryTransactionPacket.setClickPosition(Vector3f.ZERO);

				client.sendPacket(inventoryTransactionPacket);
			}
			case ABORT_DESTROY_BLOCK: {
				PlayerActionPacket playerActionPacket = new PlayerActionPacket();
				playerActionPacket.setRuntimeEntityId(runtimeId);
				playerActionPacket.setAction(PlayerActionType.ABORT_BREAK);
				playerActionPacket.setBlockPosition(blockPosition);
				playerActionPacket.setFace(packet.getDirection().ordinal());

				client.sendPacket(playerActionPacket);

				this.lastDirection = null;
				this.lastBlockPosition = null;
				TunnelMC.instance.eventManager.deregisterListener(this);
			}
		}
	}

	@Listener
	public void onEvent(EventPlayerTick event) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		int runtimeId = TunnelMC.mc.player.getId();
		PlayerActionType action = PlayerActionType.CONTINUE_BREAK;

		PlayerActionPacket playerActionPacket = new PlayerActionPacket();
		playerActionPacket.setRuntimeEntityId(runtimeId);
		playerActionPacket.setAction(action);
		playerActionPacket.setBlockPosition(this.lastBlockPosition);
		playerActionPacket.setFace(this.lastDirection.ordinal());

		BedrockConnectionAccessor.getCurrentConnection().sendPacket(playerActionPacket);
	}
}
