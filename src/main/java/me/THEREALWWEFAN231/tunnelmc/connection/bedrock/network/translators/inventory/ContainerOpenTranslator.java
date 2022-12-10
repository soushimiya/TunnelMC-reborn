package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory;

import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.translator.container.type.ContainerTypeTranslator;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

@PacketIdentifier(ContainerOpenPacket.class)
public class ContainerOpenTranslator extends PacketTranslator<ContainerOpenPacket> {

	@Override
	public void translate(ContainerOpenPacket packet, BedrockConnection bedrockConnection) {
		if (packet.getType() == ContainerType.INVENTORY) {
			return;
		}
		
		bedrockConnection.openContainerId = packet.getId();
		
		ScreenHandlerType<?> screenHandlerType = ContainerTypeTranslator.bedrockToJava(packet.getType());
		if(screenHandlerType == null) {
			System.out.println("No screen handler " + packet.getType());
			return;
		}

		NbtMap blockEntityData = bedrockConnection.blockEntityDataCache.getDataFromBlockPosition(packet.getBlockPosition());

		/*
		 * TODO: This is going to be empty sometimes because the block entity data isn't being updated all the time.
		 *  Decode the block entity data, save it in the cache and then reference it here.
 		 */
		String name = blockEntityData.getString("id");
		if (blockEntityData.getString("CustomName") != null) {
			name = blockEntityData.getString("CustomName");
		}

		OpenScreenS2CPacket openScreenS2CPacket = new OpenScreenS2CPacket(packet.getId() & 0xff, screenHandlerType, Text.of(name));
		bedrockConnection.javaConnection.processServerToClientPacket(openScreenS2CPacket);

		bedrockConnection.containers.setCurrentlyOpenContainer(new BedrockContainer(27, packet.getId()));
	}
}
