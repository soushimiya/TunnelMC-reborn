package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket.State;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;

@PacketIdentifier(RespawnPacket.class)
public class RespawnTranslator extends PacketTranslator<RespawnPacket> {

	@Override
	public void translate(RespawnPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if (TunnelMC.mc.player == null || MinecraftClient.getInstance().interactionManager == null) {
			return;
		}

		if (packet.getState() == State.SERVER_READY) {
			PlayerActionPacket playerActionPacket = new PlayerActionPacket();
			playerActionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
			playerActionPacket.setAction(PlayerActionType.RESPAWN);
			playerActionPacket.setBlockPosition(Vector3i.ZERO);
			playerActionPacket.setFace(-1);
			
			bedrockConnection.sendPacket(playerActionPacket);
			
			// TODO: Correct the dimension value so it's not just over world.
			GameMode gameMode = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();
			PlayerRespawnS2CPacket playerRespawnS2CPacket = new PlayerRespawnS2CPacket(DimensionTypes.OVERWORLD, World.OVERWORLD, -1, gameMode, gameMode, false, false, false, Optional.empty());
			javaConnection.processJavaPacket(playerRespawnS2CPacket);
		}
	}
}
