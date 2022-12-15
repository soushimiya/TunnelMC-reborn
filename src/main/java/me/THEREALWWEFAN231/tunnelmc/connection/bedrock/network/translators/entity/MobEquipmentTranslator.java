package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.mojang.datafixers.util.Pair;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;

import java.util.Collections;

@Log4j2
@PacketIdentifier(MobEquipmentPacket.class)
public class MobEquipmentTranslator extends PacketTranslator<MobEquipmentPacket> {

    @Override
    public void translate(MobEquipmentPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        EquipmentSlot equipmentSlot;
        switch (packet.getContainerId()) {
            case ContainerId.INVENTORY -> equipmentSlot = EquipmentSlot.MAINHAND;
            case ContainerId.OFFHAND -> equipmentSlot = EquipmentSlot.OFFHAND;
            default -> {
                log.error("Couldn't find the correct container id for: " + packet);
                return;
            }
        }

        Pair<EquipmentSlot, ItemStack> itemStackPair = new Pair<>(equipmentSlot, ItemTranslator.itemDataToItemStack(packet.getItem()));
        EntityEquipmentUpdateS2CPacket equipmentUpdatePacket = new EntityEquipmentUpdateS2CPacket((int) packet.getRuntimeEntityId(),
                Collections.singletonList(itemStackPair));
        javaConnection.processJavaPacket(equipmentUpdatePacket);
    }
}
