package me.THEREALWWEFAN231.tunnelmc.javaconnection.packet;

import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class CommandExecutionC2SPacketTranslator extends PacketTranslator<CommandExecutionC2SPacket> {

	@Override
	public void translate(CommandExecutionC2SPacket packet) {
		CommandRequestPacket commandPacket = new CommandRequestPacket();
		commandPacket.setCommand(packet.command());
		commandPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, Client.instance.authData.getIdentity(), "", 0));

		Client.instance.sendPacket(commandPacket);
	}

	@Override
	public Class<CommandExecutionC2SPacket> getPacketClass() {
		return CommandExecutionC2SPacket.class;
	}
}
