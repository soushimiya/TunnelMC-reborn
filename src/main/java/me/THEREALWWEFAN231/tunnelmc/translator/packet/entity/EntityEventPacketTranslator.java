package me.THEREALWWEFAN231.tunnelmc.translator.packet.entity;

import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;

public class EntityEventPacketTranslator extends PacketTranslator<EntityEventPacket> {
    @Override
    public void translate(EntityEventPacket packet) {
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
                Client.instance.javaConnection.processServerToClientPacket(swingArmPacket);
            }
        }
    }

    @Override
    public Class<EntityEventPacket> getPacketClass() {
        return EntityEventPacket.class;
    }
}
