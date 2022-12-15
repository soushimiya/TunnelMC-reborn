package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

import java.util.UUID;

@PacketIdentifier(AddItemEntityPacket.class)
public class AddItemEntityTranslator extends PacketTranslator<AddItemEntityPacket> {

	@Override
	public void translate(AddItemEntityPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int id = (int) packet.getUniqueEntityId();
		double x = packet.getPosition().getX();
		double y = packet.getPosition().getY();
		double z = packet.getPosition().getZ();
		double motionX = packet.getMotion().getX();
		double motionY = packet.getMotion().getY();
		double motionZ = packet.getMotion().getZ();
		
		EntityType<ItemEntity> entityType = EntityType.ITEM;
		ItemEntity itemEntity = entityType.create(TunnelMC.mc.world);
		itemEntity.setId(id);
		itemEntity.setPos(x, y, z);
		itemEntity.setVelocity(motionX, motionY, motionZ);
		itemEntity.setStack(ItemTranslator.itemDataToItemStack(packet.getItemInHand()));
		itemEntity.setUuid(UUID.randomUUID());
		
		javaConnection.processJavaPacket((Packet<ClientPlayPacketListener>) itemEntity.createSpawnPacket());
		
		DataTracker dataTracker = itemEntity.getDataTracker();
		EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket = new EntityTrackerUpdateS2CPacket(id, dataTracker, false);
		javaConnection.processJavaPacket(entityTrackerUpdateS2CPacket);
	}
}
