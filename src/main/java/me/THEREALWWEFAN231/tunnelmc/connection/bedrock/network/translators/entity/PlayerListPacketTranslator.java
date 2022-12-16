package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.authlib.GameProfile;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerListS2CPacket;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

@PacketIdentifier(PlayerListPacket.class)
public class PlayerListPacketTranslator extends PacketTranslator<PlayerListPacket> {

	@Override
	public void translate(PlayerListPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		PlayerListS2CPacket.Action action = packet.getAction() == PlayerListPacket.Action.ADD
				? PlayerListS2CPacket.Action.ADD_PLAYER
				: PlayerListS2CPacket.Action.REMOVE_PLAYER;

		List<Entry> removalEntries = new ArrayList<>();
		List<Entry> entries = new ArrayList<>();
		for (PlayerListPacket.Entry entry : packet.getEntries()) {
			GameProfile profile = new GameProfile(entry.getUuid(), entry.getName());

			bedrockConnection.profileNameToUuid.put(profile.getName(), profile.getId());
			bedrockConnection.serializedSkins.put(profile.getId(), entry.getSkin());

			Entry listEntry = new Entry(profile, 0, GameMode.SURVIVAL, Text.of(profile.getName()), null);

			PlayerListEntry javaEntry = javaConnection.getClientPlayNetworkHandler().getPlayerListEntry(profile.getId());
			if(packet.getAction() == PlayerListPacket.Action.ADD && javaEntry != null) {
				removalEntries.add(listEntry);
			}

			entries.add(listEntry);
		}

		if(!removalEntries.isEmpty()) {
			PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER);
			((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().addAll(removalEntries);
			javaConnection.processJavaPacket(playerListS2CPacket);
		}

		PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(action);
		((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().addAll(entries);
		javaConnection.processJavaPacket(playerListS2CPacket);
	}
}
