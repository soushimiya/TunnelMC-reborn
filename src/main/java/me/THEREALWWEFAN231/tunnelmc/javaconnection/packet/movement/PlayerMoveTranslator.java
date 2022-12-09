package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.movement;

import com.nukkitx.api.event.Listener;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputInteractionModel;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.events.EventPlayerTick;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PlayerMoveTranslator extends PacketTranslator<PlayerMoveC2SPacket> {
	private static Vector3f lastPosition = Vector3f.ZERO;
	private static Vector3f lastRotation = Vector3f.ZERO; // x for pitch, y for yaw, z for head yaw
	private static boolean lastOnGround;

	public PlayerMoveTranslator() {
		TunnelMC.instance.eventManager.registerListeners(this, this);
	}

	@Override
	public void translate(PlayerMoveC2SPacket packet) {
		//this shouldn't even be called? I don't know, doesn't matter
		PlayerMoveTranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL);
	}

	@Override
	public Class<PlayerMoveC2SPacket> getPacketClass() {
		return PlayerMoveC2SPacket.class;
	}

	public static void translateMovementPacket(PlayerMoveC2SPacket playerMoveC2SPacket, MovePlayerPacket.Mode mode) {
		if(TunnelMC.mc.player == null) {
			return;
		}

		double currentPosX = playerMoveC2SPacket.getX(TunnelMC.mc.player.getPos().x);
		double currentPosY = playerMoveC2SPacket.getY(TunnelMC.mc.player.getPos().y) + TunnelMC.mc.player.getEyeHeight(EntityPose.STANDING);
		double currentPosZ = playerMoveC2SPacket.getZ(TunnelMC.mc.player.getPos().z);
		float currentPitch = playerMoveC2SPacket.getPitch(TunnelMC.mc.player.getPitch());
		float currentYaw = playerMoveC2SPacket.getYaw(TunnelMC.mc.player.getYaw());
		float currentHeadYaw = TunnelMC.mc.player.getHeadYaw();
		boolean currentlyOnGround = playerMoveC2SPacket.isOnGround();

		int runtimeId = TunnelMC.mc.player.getId();
		Vector3f currentPos = Vector3f.from(currentPosX, currentPosY, currentPosZ);
		Vector3f currentRot = Vector3f.from(currentPitch, currentYaw, currentHeadYaw);

		switch (Client.instance.movementMode) {
			case CLIENT -> {
				if (lastPosition.equals(currentPos) && lastRotation.equals(currentRot) && lastOnGround == currentlyOnGround) {
					return;
				}

				MovePlayerPacket movePacket = new MovePlayerPacket();
				movePacket.setRuntimeEntityId(runtimeId);
				movePacket.setPosition(currentPos);
				movePacket.setRotation(Vector3f.from(currentPitch, currentYaw, currentHeadYaw));
				movePacket.setMode(mode);
				movePacket.setOnGround(currentlyOnGround);
				Client.instance.sendPacket(movePacket);
			}
			case SERVER -> {
				PlayerAuthInputPacket movePacket = new PlayerAuthInputPacket();
				movePacket.setInputMode(InputMode.MOUSE);
				movePacket.setPlayMode(ClientPlayMode.NORMAL);
				movePacket.setInputInteractionModel(InputInteractionModel.CROSSHAIR);
				movePacket.setVrGazeDirection(Vector3f.ZERO);
				movePacket.setDelta(currentPos.sub(lastPosition));
				movePacket.setMotion(movePacket.getDelta().toVector2(true));
				movePacket.setPosition(currentPos);
				movePacket.setRotation(Vector3f.from(currentPitch, currentYaw, currentYaw)); // Set yaw twice so BDS cooperates with head movement better

				playerAuthInputPacket = movePacket;
			}
		}

		PlayerMoveTranslator.lastPosition = currentPos;
		PlayerMoveTranslator.lastRotation = currentRot;
		PlayerMoveTranslator.lastOnGround = currentlyOnGround;
	}

	// For server authoritative movement, a vanilla client sends this packet every tick
	private static PlayerAuthInputPacket playerAuthInputPacket;

	@Listener
	public void onEvent(EventPlayerTick event) {
		if(playerAuthInputPacket == null) {
			return;
		}

		playerAuthInputPacket.setTick(event.tick());
		playerAuthInputPacket.getInputData().clear();
		if(Client.instance.startedSprinting.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.START_SPRINTING);
		}
		if(Client.instance.stoppedSprinting.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.STOP_SPRINTING);
		}
		if(Client.instance.startedSneaking.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.START_SNEAKING);
		}
		if(Client.instance.stoppedSneaking.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.STOP_SNEAKING);
		}
		Client.instance.sendPacket(playerAuthInputPacket);
	}
}
