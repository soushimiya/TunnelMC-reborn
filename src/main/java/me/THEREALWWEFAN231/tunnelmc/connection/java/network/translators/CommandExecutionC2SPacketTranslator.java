package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

@PacketIdentifier(CommandExecutionC2SPacket.class)
public class CommandExecutionC2SPacketTranslator extends PacketTranslator<CommandExecutionC2SPacket> {

	@Override
	public void translate(CommandExecutionC2SPacket packet, Client client) {
		CommandRequestPacket commandPacket = new CommandRequestPacket();
		commandPacket.setCommand("/" + packet.command());
		commandPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, client.authData.identity(), "", 0));

		client.sendPacket(commandPacket);
	}
}
