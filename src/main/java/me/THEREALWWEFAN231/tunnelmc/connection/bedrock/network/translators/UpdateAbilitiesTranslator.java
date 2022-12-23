package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.data.Ability;
import com.nukkitx.protocol.bedrock.data.AbilityLayer;
import com.nukkitx.protocol.bedrock.packet.UpdateAbilitiesPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;

@Log4j2
@PacketIdentifier(UpdateAbilitiesPacket.class)
public class UpdateAbilitiesTranslator extends PacketTranslator<UpdateAbilitiesPacket> {

    @Override
    public void translate(UpdateAbilitiesPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if(packet.getUniqueEntityId() != bedrockConnection.uniqueId) {
            return;
        }

        PlayerAbilities abilities = new PlayerAbilities();
        for (AbilityLayer layer : packet.getAbilityLayers()) {
            for(Ability ability : layer.getAbilitiesSet()) {
                boolean enabled = layer.getAbilityValues().contains(ability);

                switch (ability) {
                    case FLY_SPEED -> {
                        if(enabled) {
                            abilities.setFlySpeed(layer.getFlySpeed());
                        }
                    }
                    case WALK_SPEED -> {
                        if(enabled) {
                            abilities.setWalkSpeed(layer.getWalkSpeed());
                        }
                    }
                    case MAY_FLY -> abilities.allowFlying = enabled;
                    case FLYING -> abilities.flying = enabled;
                    case INVULNERABLE -> abilities.invulnerable = enabled;
                }
            }

            abilities.allowModifyWorld = layer.getAbilityValues().contains(Ability.BUILD)
                    && layer.getAbilityValues().contains(Ability.MINE);
        }

        PlayerAbilitiesS2CPacket abilitiesPacket = new PlayerAbilitiesS2CPacket(abilities);
        javaConnection.processJavaPacket(abilitiesPacket);
    }
}
