package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.TakeItemEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;

@PacketIdentifier(TakeItemEntityPacket.class)
public class TakeItemEntityPacketTranslator extends PacketTranslator<TakeItemEntityPacket> {

	@Override
	public void translate(TakeItemEntityPacket packet, Client client) {
		int entityId = (int) packet.getItemRuntimeEntityId();
		int collectorId = (int) packet.getRuntimeEntityId();
		int stackAmount = 1; // TODO: This needs the correct value but we can probably get the value from the item entity in the world.

		ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket = new ItemPickupAnimationS2CPacket(entityId, collectorId, stackAmount);

		client.javaConnection.processServerToClientPacket(itemPickupAnimationS2CPacket);
	}
}
