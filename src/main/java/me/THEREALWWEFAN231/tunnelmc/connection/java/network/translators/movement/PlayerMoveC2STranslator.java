package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.api.event.Listener;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputInteractionModel;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerTickEvent;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@PacketIdentifier(PlayerMoveC2SPacket.class)
public class PlayerMoveC2STranslator extends PacketTranslator<PlayerMoveC2SPacket> {
	private static Vector3f lastPosition = Vector3f.ZERO;
	private static Vector3f lastRotation = Vector3f.ZERO; // x for pitch, y for yaw, z for head yaw
	private static boolean lastOnGround;

	public PlayerMoveC2STranslator() {
		TunnelMC.getInstance().getEventManager().registerListeners(this, this);
	}

	@Override
	public void translate(PlayerMoveC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		//this shouldn't even be called? I don't know, doesn't matter
		PlayerMoveC2STranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.NORMAL, bedrockConnection);
	}

	public static void translateMovementPacket(PlayerMoveC2SPacket playerMoveC2SPacket, MovePlayerPacket.Mode mode, BedrockConnection bedrockConnection) { // TODO: not here
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

		switch (bedrockConnection.movementMode) {
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
				bedrockConnection.sendPacket(movePacket);
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

		PlayerMoveC2STranslator.lastPosition = currentPos;
		PlayerMoveC2STranslator.lastRotation = currentRot;
		PlayerMoveC2STranslator.lastOnGround = currentlyOnGround;
	}

	// For server authoritative movement, a vanilla client sends this packet every tick
	private static PlayerAuthInputPacket playerAuthInputPacket;

	@Listener
	public void onEvent(PlayerTickEvent event) {
		if(playerAuthInputPacket == null) {
			return;
		}
		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();

		playerAuthInputPacket.setTick(event.getTick());
		if(bedrockConnection.startedSprinting.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.START_SPRINTING);
		}
		if(bedrockConnection.stoppedSprinting.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.STOP_SPRINTING);
		}
		if(bedrockConnection.startedSneaking.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.START_SNEAKING);
		}
		if(bedrockConnection.stoppedSneaking.compareAndSet(true, false)) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.STOP_SNEAKING);
		}
		if(bedrockConnection.jumping.get()) {
			playerAuthInputPacket.getInputData().add(PlayerAuthInputData.JUMPING);
		}
		if(bedrockConnection.authInputData.contains(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS)) {
			if(playerAuthInputPacket.getPlayerActions().addAll(bedrockConnection.blockActions)) {
				bedrockConnection.blockActions.clear();
			}
		}
		if(playerAuthInputPacket.getInputData().addAll(bedrockConnection.authInputData)) {
			bedrockConnection.authInputData.clear();
		}
		bedrockConnection.sendPacket(playerAuthInputPacket);
		playerAuthInputPacket.getInputData().clear();
	}
}
