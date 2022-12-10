package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

@PacketIdentifier(ClientCommandC2SPacket.class)
public class ClientCommandC2STranslator extends PacketTranslator<ClientCommandC2SPacket> {

	@Override
	public void translate(ClientCommandC2SPacket packet, Client client) {
		if (TunnelMC.mc.player == null) {
			return;
		}

		switch (packet.getMode()) {
			case PRESS_SHIFT_KEY -> client.startedSneaking.set(true);
			case RELEASE_SHIFT_KEY -> client.stoppedSneaking.set(true);
			case START_SPRINTING -> client.startedSprinting.set(true);
			case STOP_SPRINTING -> client.stoppedSprinting.set(true);
		}

		PlayerActionType actionType = switch (packet.getMode()) {
			case PRESS_SHIFT_KEY -> PlayerActionType.START_SNEAK;
			case RELEASE_SHIFT_KEY -> PlayerActionType.STOP_SNEAK;
			case STOP_SLEEPING -> null;
			case START_SPRINTING -> PlayerActionType.START_SPRINT;
			case STOP_SPRINTING -> PlayerActionType.STOP_SPRINT;
			case START_RIDING_JUMP -> null;
			case STOP_RIDING_JUMP -> null;
			case OPEN_INVENTORY -> null;
			case START_FALL_FLYING -> null;
		};
		if(actionType == null && client.movementMode != AuthoritativeMovementMode.CLIENT) {
			return;
		}

		PlayerActionPacket playerActionPacket = new PlayerActionPacket();
		playerActionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
		playerActionPacket.setAction(actionType);
		playerActionPacket.setBlockPosition(Vector3i.ZERO);

		client.sendPacket(playerActionPacket);
	}
}
