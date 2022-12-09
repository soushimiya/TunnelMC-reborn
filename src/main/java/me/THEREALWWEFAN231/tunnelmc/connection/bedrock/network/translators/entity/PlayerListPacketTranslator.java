package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.authlib.GameProfile;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

@PacketIdentifier(PlayerListPacket.class)
public class PlayerListPacketTranslator extends PacketTranslator<PlayerListPacket> {

	@Override
	public void translate(PlayerListPacket packet, Client client) {
		boolean add = packet.getAction() == PlayerListPacket.Action.ADD;
		List<Entry> entries = new ArrayList<>();

		PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(add ? PlayerListS2CPacket.Action.ADD_PLAYER : PlayerListS2CPacket.Action.REMOVE_PLAYER);
		for (PlayerListPacket.Entry entry : packet.getEntries()) {
			// gamemode says nullable but is used in ClientGameSession/:
			entries.add(new Entry(new GameProfile(entry.getUuid(), entry.getName()), 0, GameMode.SURVIVAL, Text.of(entry.getName()), null));
		}

		((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().addAll(entries);
		client.javaConnection.processServerToClientPacket(playerListS2CPacket);
	}
}
