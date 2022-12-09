package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;

@PacketIdentifier(BlockEntityDataPacket.class)
public class BlockEntityDataPacketTranslator extends PacketTranslator<BlockEntityDataPacket> {

	@Override
	public void translate(BlockEntityDataPacket packet, Client client) {
		Vector3i blockPosition = packet.getBlockPosition();
		NbtMap blockEntityData = packet.getData();
		
		client.blockEntityDataCache.getCachedBlockPositionsData().put(blockPosition, blockEntityData);
	}
}
