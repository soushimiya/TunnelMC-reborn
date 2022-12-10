package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.datafixers.util.Pair;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;

import java.util.Collections;

@PacketIdentifier(MobEquipmentPacket.class)
public class MobEquipmentTranslator extends PacketTranslator<MobEquipmentPacket> {

    @Override
    public void translate(MobEquipmentPacket packet, BedrockConnection bedrockConnection) {
        EquipmentSlot equipmentSlot;
        switch (packet.getContainerId()) {
            case ContainerId.INVENTORY -> equipmentSlot = EquipmentSlot.MAINHAND;
            case ContainerId.OFFHAND -> equipmentSlot = EquipmentSlot.OFFHAND;
            default -> {
                System.out.println("Not sure how to handle MobEquipmentPacket: " + packet.toString());
                return;
            }
        }

        Pair<EquipmentSlot, ItemStack> itemStackPair = new Pair<>(equipmentSlot, ItemTranslator.itemDataToItemStack(packet.getItem()));
        EntityEquipmentUpdateS2CPacket equipmentUpdatePacket = new EntityEquipmentUpdateS2CPacket((int) packet.getRuntimeEntityId(),
                Collections.singletonList(itemStackPair));
        bedrockConnection.javaConnection.processServerToClientPacket(equipmentUpdatePacket);
    }
}
