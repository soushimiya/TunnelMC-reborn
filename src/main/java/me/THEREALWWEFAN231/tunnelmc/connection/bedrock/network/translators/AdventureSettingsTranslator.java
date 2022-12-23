package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;

@Deprecated
@PacketIdentifier(AdventureSettingsPacket.class)
public class AdventureSettingsTranslator extends PacketTranslator<AdventureSettingsPacket> { // TODO: UPDATE THIS

    @Override
    public void translate(AdventureSettingsPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        PlayerAbilities abilities = new PlayerAbilities();
        abilities.allowFlying = packet.getSettings().contains(AdventureSetting.MAY_FLY);
        abilities.allowModifyWorld = packet.getSettings().contains(AdventureSetting.BUILD);
        abilities.flying = packet.getSettings().contains(AdventureSetting.FLYING);
        abilities.invulnerable = false;

        PlayerAbilitiesS2CPacket abilitiesPacket = new PlayerAbilitiesS2CPacket(abilities);
        javaConnection.processJavaPacket(abilitiesPacket);
    }
}
