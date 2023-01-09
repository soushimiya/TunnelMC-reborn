package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.api.event.Listener;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.data.PlayerBlockActionData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerTickEvent;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

@Log4j2
@PacketIdentifier(PlayerActionC2SPacket.class)
public class PlayerActionC2STranslator extends PacketTranslator<PlayerActionC2SPacket> {
	private Direction lastDirection;
	private Vector3i lastBlockPosition;

	@Override
	public void translate(PlayerActionC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
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
				BedrockPacket pk = getPlayerActionPacket(bedrockConnection, playerActionPacket);
				if(pk != null) {
					bedrockConnection.sendPacket(pk);
				}

				TunnelMC.getInstance().getEventManager().registerListeners(bedrockConnection, this);

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
				BedrockPacket pk = getPlayerActionPacket(bedrockConnection, playerActionPacket);
				if(pk != null) {
					bedrockConnection.sendPacket(pk);
				}

				if (MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
					PlayerActionPacket creativePacket = new PlayerActionPacket();
					creativePacket.setRuntimeEntityId(runtimeId);
					creativePacket.setAction(PlayerActionType.DIMENSION_CHANGE_REQUEST_OR_CREATIVE_DESTROY_BLOCK);
					creativePacket.setBlockPosition(blockPosition);
					creativePacket.setFace(packet.getDirection().ordinal());

					bedrockConnection.sendPacket(creativePacket);
				}

				this.lastDirection = null;
				this.lastBlockPosition = null;
				TunnelMC.getInstance().getEventManager().deregisterListener(this);

				InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
				inventoryTransactionPacket.setTransactionType(TransactionType.ITEM_USE);
				inventoryTransactionPacket.setActionType(2);
				inventoryTransactionPacket.setBlockPosition(blockPosition);
				inventoryTransactionPacket.setBlockFace(packet.getDirection().ordinal());
				inventoryTransactionPacket.setHotbarSlot(TunnelMC.mc.player.getInventory().selectedSlot);
				inventoryTransactionPacket.setItemInHand(bedrockConnection.getWrappedContainers().getPlayerInventory().getItemFromSlot(TunnelMC.mc.player.getInventory().selectedSlot));
				inventoryTransactionPacket.setPlayerPosition(Vector3f.from(TunnelMC.mc.player.getPos().x, TunnelMC.mc.player.getPos().y, TunnelMC.mc.player.getPos().z));
				inventoryTransactionPacket.setClickPosition(Vector3f.ZERO);

				bedrockConnection.sendPacket(inventoryTransactionPacket);
			}
			case ABORT_DESTROY_BLOCK: {
				PlayerActionPacket playerActionPacket = new PlayerActionPacket();
				playerActionPacket.setRuntimeEntityId(runtimeId);
				playerActionPacket.setAction(PlayerActionType.ABORT_BREAK);
				playerActionPacket.setBlockPosition(blockPosition);
				playerActionPacket.setFace(packet.getDirection().ordinal());
				BedrockPacket pk = getPlayerActionPacket(bedrockConnection, playerActionPacket);
				if(pk != null) {
					bedrockConnection.sendPacket(pk);
				}

				this.lastDirection = null;
				this.lastBlockPosition = null;
				TunnelMC.getInstance().getEventManager().deregisterListener(this);
			}
		}
	}

	private BedrockPacket getPlayerActionPacket(BedrockConnection bedrockConnection, PlayerActionPacket packet) {
		switch (bedrockConnection.movementMode) {
			case CLIENT -> {
				return packet;
			}
			case SERVER, SERVER_WITH_REWIND -> {
				PlayerBlockActionData actionData = new PlayerBlockActionData();
				actionData.setAction(packet.getAction());
				actionData.setFace(packet.getFace());
				actionData.setBlockPosition(packet.getBlockPosition());

				bedrockConnection.authInputData.add(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS);
				bedrockConnection.blockActions.add(actionData);
			}
			default -> log.error("Cannot translate " + bedrockConnection.movementMode);
		}
		return null;
	}

	@Listener
	public void onEvent(PlayerTickEvent event) {
		if (TunnelMC.mc.player == null) {
			return;
		}
		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
		int runtimeId = TunnelMC.mc.player.getId();

		PlayerActionPacket playerActionPacket = new PlayerActionPacket();
		playerActionPacket.setRuntimeEntityId(runtimeId);
		playerActionPacket.setAction(PlayerActionType.CONTINUE_BREAK);
		playerActionPacket.setBlockPosition(this.lastBlockPosition);
		playerActionPacket.setFace(this.lastDirection.ordinal());
		BedrockPacket pk = getPlayerActionPacket(bedrockConnection, playerActionPacket);
		if(pk == null) {
			return;
		}

		bedrockConnection.sendPacket(pk);
	}
}
