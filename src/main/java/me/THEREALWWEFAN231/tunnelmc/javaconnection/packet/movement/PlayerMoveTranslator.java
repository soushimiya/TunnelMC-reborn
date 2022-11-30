package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.movement;

import com.darkmagician6.eventapi.EventTarget;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputInteractionModel;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;

import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.events.EventPlayerTick;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.Iterator;

public class PlayerMoveTranslator extends PacketTranslator<PlayerMoveC2SPacket> {
	private static double lastPosX;
	private static double lastPosY = -Double.MAX_VALUE;
	private static double lastPosZ;
	private static float lastYaw;
	private static float lastPitch;
	private static boolean lastOnGround;
	private static long currentTick;

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
		double currentPosX = playerMoveC2SPacket.getX(TunnelMC.mc.player.getPos().x);
		double currentPosY = playerMoveC2SPacket.getY(TunnelMC.mc.player.getPos().y) + TunnelMC.mc.player.getEyeHeight(EntityPose.STANDING);
		double currentPosZ = playerMoveC2SPacket.getZ(TunnelMC.mc.player.getPos().z);
		float currentYaw = playerMoveC2SPacket.getYaw(TunnelMC.mc.player.getYaw());
		float currentPitch = playerMoveC2SPacket.getPitch(TunnelMC.mc.player.getPitch());
		boolean currentlyOnGround = playerMoveC2SPacket.isOnGround();

		if (PlayerMoveTranslator.lastPosX == currentPosX && PlayerMoveTranslator.lastPosY == currentPosY
				&& PlayerMoveTranslator.lastPosZ == currentPosZ && PlayerMoveTranslator.lastYaw == currentYaw
				&& PlayerMoveTranslator.lastPitch == currentPitch && PlayerMoveTranslator.lastOnGround == currentlyOnGround) {
			return;
		}

		int runtimeId = TunnelMC.mc.player.getId();
		Vector3f currentPos = Vector3f.from(currentPosX, currentPosY, currentPosZ);

		switch (Client.instance.movementMode) {
			case CLIENT -> {
				MovePlayerPacket movePacket = new MovePlayerPacket();
				movePacket.setRuntimeEntityId(runtimeId);
				movePacket.setPosition(currentPos);
				movePacket.setRotation(Vector3f.from(currentPitch, currentYaw, currentYaw)); // Set yaw twice so BDS cooperates with head movement better
				movePacket.setMode(mode);
				movePacket.setOnGround(currentlyOnGround);
				Client.instance.sendPacket(movePacket);
			}
			case SERVER -> {
				Vector3f previousPos = Vector3f.from(lastPosX, lastPosY, lastPosZ);

				PlayerAuthInputPacket movePacket = new PlayerAuthInputPacket();
				movePacket.setInputMode(InputMode.MOUSE);
				movePacket.setInputInteractionModel(InputInteractionModel.CROSSHAIR);
				movePacket.setDelta(currentPos.sub(previousPos));
				movePacket.setPosition(currentPos);
				movePacket.setRotation(Vector3f.from(currentPitch, currentYaw, currentYaw)); // Set yaw twice so BDS cooperates with head movement better
				movePacket.setTick(currentTick);

				if(Client.instance.startedSprinting.compareAndSet(true, false)) {
					movePacket.getInputData().add(PlayerAuthInputData.START_SPRINTING);
				}
				if(Client.instance.stoppedSprinting.compareAndSet(true, false)) {
					movePacket.getInputData().add(PlayerAuthInputData.STOP_SPRINTING);
				}
				if(Client.instance.startedSneaking.compareAndSet(true, false)) {
					movePacket.getInputData().add(PlayerAuthInputData.START_SNEAKING);
				}
				if(Client.instance.stoppedSneaking.compareAndSet(true, false)) {
					movePacket.getInputData().add(PlayerAuthInputData.STOP_SNEAKING);
				}
			}
		}

		PlayerMoveTranslator.lastPosX = currentPosX;
		PlayerMoveTranslator.lastPosY = currentPosY;
		PlayerMoveTranslator.lastPosZ = currentPosZ;
		PlayerMoveTranslator.lastYaw = currentYaw;
		PlayerMoveTranslator.lastPitch = currentPitch;
		PlayerMoveTranslator.lastOnGround = currentlyOnGround;
	}

	@EventTarget
	public void onEvent(EventPlayerTick event) {
		currentTick++;
	}
}
