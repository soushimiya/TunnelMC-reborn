package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.authlib.GameProfile;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerListS2CPacket;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
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
		PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(action);

		List<Entry> entries = new ArrayList<>();
		for (PlayerListPacket.Entry entry : packet.getEntries()) {
			if(packet.getAction() == PlayerListPacket.Action.ADD
					&& javaConnection.getClientPlayNetworkHandler().getPlayerListEntry(entry.getUuid()) != null) {
				return;
			}

			bedrockConnection.profileNameToUuid.put(entry.getName(), entry.getUuid());
			entries.add(new Entry(new GameProfile(entry.getUuid(), entry.getName()), 0, GameMode.SURVIVAL, Text.of(entry.getName()), null));
		}

		((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().addAll(entries);
		javaConnection.processJavaPacket(playerListS2CPacket);
	}
}
