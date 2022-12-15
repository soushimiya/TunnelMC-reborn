package me.THEREALWWEFAN231.tunnelmc.connection.java.network;

import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.*;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.FullMoveC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.LookAndOnGroundMoveC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.PlayerMoveC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement.PositionAndOnGroundMoveC2STranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslatorManager;
import net.minecraft.network.Packet;

public class JavaPacketTranslatorManager extends PacketTranslatorManager<Packet<?>> {

	public JavaPacketTranslatorManager() {
		super();
		this.addTranslator(new HandSwingC2STranslator());
		this.addTranslator(new PlayerMoveC2STranslator());
		this.addTranslator(new LookAndOnGroundMoveC2STranslator());
		this.addTranslator(new PositionAndOnGroundMoveC2STranslator());
		this.addTranslator(new FullMoveC2STranslator());
		this.addTranslator(new ChatMessageC2STranslator());
		this.addTranslator(new UpdateSelectedSlotC2STranslator());
		this.addTranslator(new PlayerActionC2STranslator());
		this.addTranslator(new PlayerInteractBlockC2STranslator());
		this.addTranslator(new PlayerInteractItemC2STranslator());
		this.addTranslator(new PlayerInteractEntityC2STranslator());
		this.addTranslator(new ClientCommandC2STranslator());
		this.addTranslator(new CloseHandledScreenC2STranslator());
		this.addTranslator(new ClickSlotC2STranslator());
		this.addTranslator(new UpdatePlayerAbilitiesC2STranslator());
		this.addTranslator(new ClientStatusC2STranslator());
		this.addTranslator(new CommandExecutionC2STranslator());
	}
}
