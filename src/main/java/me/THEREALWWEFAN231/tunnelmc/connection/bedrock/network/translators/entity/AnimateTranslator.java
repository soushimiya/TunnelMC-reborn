package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;

@PacketIdentifier(AnimatePacket.class)
public class AnimateTranslator extends PacketTranslator<AnimatePacket> {

    @Override
    public void translate(AnimatePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (TunnelMC.mc.world == null) {
            return;
        }

        Entity entity = TunnelMC.mc.world.getEntityById((int) packet.getRuntimeEntityId());
        if (entity == null) {
            return;
        }

        Integer action = switch (packet.getAction()) {
            case WAKE_UP -> EntityAnimationS2CPacket.WAKE_UP;
            case SWING_ARM -> EntityAnimationS2CPacket.SWING_MAIN_HAND;
            case CRITICAL_HIT -> EntityAnimationS2CPacket.CRIT;
            case MAGIC_CRITICAL_HIT -> EntityAnimationS2CPacket.ENCHANTED_HIT;
            default -> null;
        };
        if(action == null) {
            return;
        }

        EntityAnimationS2CPacket swingArmPacket = new EntityAnimationS2CPacket(entity, action);
        javaConnection.processJavaPacket(swingArmPacket);
    }
}
