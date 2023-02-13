package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerEntity;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerListS2CPacket;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.Collections;
import java.util.UUID;

@PacketIdentifier(AddPlayerPacket.class)
public class AddPlayerTranslator extends PacketTranslator<AddPlayerPacket> {

	@Override
	public void translate(AddPlayerPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if (TunnelMC.mc.world == null) {
			return;
		}

		int id = (int) packet.getRuntimeEntityId();
		UUID uuid = packet.getUuid();
		String name = packet.getUsername();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY();
		double z = packet.getPosition().getZ();
		float pitch = packet.getRotation().getX();
		float yaw = packet.getRotation().getY();
		float headYaw = packet.getRotation().getZ();
		Vec3d velocity = new Vec3d(packet.getMotion().getX(), packet.getMotion().getY(), packet.getMotion().getZ());

		TunnelMC.mc.executeSync(() -> {
			GameProfile profile = new GameProfile(uuid, name);
			OtherClientPlayerEntity player = new OtherClientPlayerEntity(TunnelMC.mc.world, profile, null);
			player.setId(id);
			player.setPos(x, y, z);
			player.setYaw(yaw);
			player.setHeadYaw(headYaw);
			player.setPitch(pitch);
			player.setVelocity(velocity);

			if(javaConnection.getClientPlayNetworkHandler().getPlayerListEntry(uuid) == null) {
				PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER);
				((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().add(new PlayerListS2CPacket.Entry(
						profile, 0, GameMode.SURVIVAL, Text.of(profile.getName()), null));
				javaConnection.processJavaPacket(playerListS2CPacket);
			}

			PlayerSpawnS2CPacket playerSpawnS2CPacket = new PlayerSpawnS2CPacket(player);
			javaConnection.processJavaPacket(playerSpawnS2CPacket);
			player.updateTrackedHeadRotation(headYaw, 3);

			Pair<EquipmentSlot, ItemStack> itemStackPair = new Pair<>(EquipmentSlot.MAINHAND, ItemTranslator.itemDataToItemStack(packet.getHand()));
			EntityEquipmentUpdateS2CPacket equipmentUpdatePacket = new EntityEquipmentUpdateS2CPacket((int) packet.getRuntimeEntityId(),
					Collections.singletonList(itemStackPair));
			javaConnection.processJavaPacket(equipmentUpdatePacket);

			bedrockConnection.getEntityMetadataTranslatorManager().translateData(
					it.unimi.dsi.fastutil.Pair.of(player, packet.getMetadata()), bedrockConnection, javaConnection);

			player.getDataTracker().set(IMixinPlayerEntity.PLAYER_MODEL_PARTS(), (byte) (PlayerModelPart.JACKET.getBitFlag()
					| PlayerModelPart.HAT.getBitFlag()
					| PlayerModelPart.LEFT_SLEEVE.getBitFlag()
					| PlayerModelPart.LEFT_PANTS_LEG.getBitFlag()
					| PlayerModelPart.RIGHT_SLEEVE.getBitFlag()
					| PlayerModelPart.RIGHT_PANTS_LEG.getBitFlag()));
			EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket = new EntityTrackerUpdateS2CPacket(id, player.getDataTracker(), true);
			javaConnection.processJavaPacket(entityTrackerUpdateS2CPacket);
		});
	}
}
