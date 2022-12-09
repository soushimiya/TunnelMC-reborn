package me.THEREALWWEFAN231.tunnelmc.connection.java.network;

import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.*;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.BothTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.LookOnlyTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.PlayerMoveTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.PositionOnlyTranslator;
import net.minecraft.network.Packet;

public class JavaPacketTranslatorManager extends PacketTranslatorManager<Packet<?>> {

	public JavaPacketTranslatorManager() {
		this.addTranslator(new HandSwingC2SPacketTranslator());
		this.addTranslator(new PlayerMoveTranslator());
		this.addTranslator(new LookOnlyTranslator());
		this.addTranslator(new PositionOnlyTranslator());
		this.addTranslator(new BothTranslator());
		this.addTranslator(new ChatMessageC2SPacketTranslator());
		this.addTranslator(new UpdateSelectedSlotC2SPacketTranslator());
		this.addTranslator(new PlayerActionC2STranslator());
		this.addTranslator(new PlayerInteractBlockC2SPacketTranslator());
		this.addTranslator(new PlayerInteractItemC2SPacketTranslator());
		this.addTranslator(new PlayerInteractEntityC2SPacketTranslator());
		this.addTranslator(new ClientCommandC2SPacketTranslator());
		this.addTranslator(new CloseHandledScreenC2SPacketTranslator());
		this.addTranslator(new ClickSlotC2SPacketTranslator());
		this.addTranslator(new UpdatePlayerAbilitiesC2STranslator());
		this.addTranslator(new ClientStatusC2SPacketTranslator());
		this.addTranslator(new CommandExecutionC2SPacketTranslator());
	}
}
