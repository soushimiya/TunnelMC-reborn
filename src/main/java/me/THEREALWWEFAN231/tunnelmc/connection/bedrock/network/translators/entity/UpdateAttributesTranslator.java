package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.data.AttributeData;
import com.nukkitx.protocol.bedrock.packet.UpdateAttributesPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

@Log4j2
@PacketIdentifier(UpdateAttributesPacket.class)
public class UpdateAttributesTranslator extends PacketTranslator<UpdateAttributesPacket> {
    private float health = 20;
    private int food = 20;
    private float saturation = 5;
    private int level = 1;
    private float experience = 0;

    @Override
    public void translate(UpdateAttributesPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        int id = (int) packet.getRuntimeEntityId();
        if (TunnelMC.mc.player.getId() != id) {
            return;
        }

        for(AttributeData attributeData : packet.getAttributes()) {
            switch(attributeData.getName()) {
                case "minecraft:health" -> this.health = attributeData.getValue();
                case "minecraft:player.hunger" -> this.food = (int) attributeData.getValue();
                case "minecraft:player.saturation" -> this.saturation = attributeData.getValue();
                case "minecraft:player.level" -> this.level = (int) attributeData.getValue();
                case "minecraft:player.experience" -> this.experience = attributeData.getValue();
            }
        }

        javaConnection.processJavaPacket(new HealthUpdateS2CPacket(this.health, this.food, this.saturation));
        javaConnection.processJavaPacket(new ExperienceBarUpdateS2CPacket(this.experience, this.level, getTotalExperience(this.level)));
    }

    private int getTotalExperience(int level) {
        if(level <= 16) {
            return MathHelper.square(level) + 6 * level;
        }else if(level <= 31) {
            return (int) (2.5 * MathHelper.square(level) - 40.5 * level + 360);
        }

        return (int) (4.5 * MathHelper.square(level) - 162.5 * level + 2220);
    }

    @Override
    public boolean idleUntil(UpdateAttributesPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        return TunnelMC.mc.player != null;
    }
}
