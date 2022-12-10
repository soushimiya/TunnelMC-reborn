package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;

@PacketIdentifier(EntityEventPacket.class)
public class EntityEventTranslator extends PacketTranslator<EntityEventPacket> {

    @Override
    public void translate(EntityEventPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (TunnelMC.mc.world == null) {
            return;
        }

        Entity entity = TunnelMC.mc.world.getEntityById((int) packet.getRuntimeEntityId());
        if (entity == null) {
            return;
        }

        switch (packet.getType()) {
            case ATTACK_START -> {
                EntityAnimationS2CPacket swingArmPacket = new EntityAnimationS2CPacket(entity, EntityAnimationS2CPacket.SWING_MAIN_HAND);
                javaConnection.processJavaPacket(swingArmPacket);
            }
        }
    }
}
