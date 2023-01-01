package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.GenericContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.container.type.ContainerTypeTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

@Log4j2
@PacketIdentifier(ContainerOpenPacket.class)
public class ContainerOpenTranslator extends PacketTranslator<ContainerOpenPacket> {

	@Override
	public void translate(ContainerOpenPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if (packet.getType() == ContainerType.INVENTORY) {
			return;
		}
		
		ScreenHandlerType<?> screenHandlerType = ContainerTypeTranslator.bedrockToJava(packet.getType());
		if(screenHandlerType == null) {
			log.error("Couldn't find the correct screen handler for: " + packet);
			return;
		}

		NbtMap blockEntityData = bedrockConnection.getBlockEntityDataCache().getDataFromBlockPosition(packet.getBlockPosition());

		/*
		 * TODO: This is going to be empty sometimes because the block entity data isn't being updated all the time.
		 *  Decode the block entity data, save it in the cache and then reference it here.
 		 */
		String name = blockEntityData.getString("id");
		if (blockEntityData.getString("CustomName") != null) {
			name = blockEntityData.getString("CustomName");
		}

		OpenScreenS2CPacket openScreenS2CPacket = new OpenScreenS2CPacket(packet.getId() & 0xff, screenHandlerType, Text.of(name));
		javaConnection.processJavaPacket(openScreenS2CPacket);

		bedrockConnection.getWrappedContainers().setCurrentlyOpenContainer(packet.getId(), new GenericContainer(27));
	}
}
