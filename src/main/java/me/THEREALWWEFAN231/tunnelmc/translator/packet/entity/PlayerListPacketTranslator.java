package me.THEREALWWEFAN231.tunnelmc.translator.packet.entity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerListS2CPacket;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class PlayerListPacketTranslator extends PacketTranslator<PlayerListPacket> {

	@Override
	public void translate(PlayerListPacket packet) {
		boolean add = packet.getAction() == PlayerListPacket.Action.ADD;
		List<Entry> entries = new ArrayList<>();

		PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(add ? PlayerListS2CPacket.Action.ADD_PLAYER : PlayerListS2CPacket.Action.REMOVE_PLAYER);
		for (PlayerListPacket.Entry entry : packet.getEntries()) {
			// gamemode says nullable but is used in ClientGameSession/:
			entries.add(new Entry(new GameProfile(entry.getUuid(), entry.getName()), 0, GameMode.SURVIVAL, Text.of(entry.getName()), null));
		}

		((IMixinPlayerListS2CPacket) playerListS2CPacket).getEntries().addAll(entries);
		Client.instance.javaConnection.processServerToClientPacket(playerListS2CPacket);
	}

	@Override
	public Class<PlayerListPacket> getPacketClass() {
		return PlayerListPacket.class;
	}
}
