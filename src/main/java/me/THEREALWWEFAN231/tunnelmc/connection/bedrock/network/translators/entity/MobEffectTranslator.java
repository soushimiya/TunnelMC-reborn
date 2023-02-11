package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.MobEffectPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Log4j2
@PacketIdentifier(MobEffectPacket.class)
public class MobEffectTranslator extends PacketTranslator<MobEffectPacket> {

    @Override
    public void translate(MobEffectPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        StatusEffect statusEffect = StatusEffect.byRawId(packet.getEffectId());
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, packet.getDuration(),
                packet.getAmplifier(), false, packet.isParticles(), true);

        TunnelMC.mc.executeSync(() -> {
            Entity entity = TunnelMC.mc.world.getEntityById((int) packet.getRuntimeEntityId());
            if (entity == null) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity) entity;

            switch (packet.getEvent()) {
                case MODIFY, ADD -> {
                    boolean changed = livingEntity.addStatusEffect(statusEffectInstance);
                    if(changed) {
                        if(packet.getEvent() == MobEffectPacket.Event.MODIFY) {
                            statusEffect.onRemoved(livingEntity, livingEntity.getAttributes(), statusEffectInstance.getAmplifier());
                        }
                        statusEffect.onApplied(livingEntity, livingEntity.getAttributes(), statusEffectInstance.getAmplifier());
                    }
                }
                case REMOVE -> {
                    boolean changed = livingEntity.removeStatusEffect(statusEffect);
                    if(changed) {
                        statusEffect.onRemoved(livingEntity, livingEntity.getAttributes(), statusEffectInstance.getAmplifier());
                    }
                }
            }
        });
    }
}
