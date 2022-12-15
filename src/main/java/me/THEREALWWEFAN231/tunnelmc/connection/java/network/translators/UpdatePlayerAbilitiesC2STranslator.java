package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import com.nukkitx.protocol.bedrock.data.PlayerPermission;
import com.nukkitx.protocol.bedrock.data.command.CommandPermission;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;

@PacketIdentifier(UpdatePlayerAbilitiesC2SPacket.class)
public class UpdatePlayerAbilitiesC2STranslator extends PacketTranslator<UpdatePlayerAbilitiesC2SPacket> {

    @Override
    public void translate(UpdatePlayerAbilitiesC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) { // TODO: update this
        AdventureSettingsPacket settingsPacket = new AdventureSettingsPacket();
        if (packet.isFlying()) {
            // Otherwise certain updates can stop the player from flying
            settingsPacket.getSettings().add(AdventureSetting.FLYING);
        }
        settingsPacket.setPlayerPermission(PlayerPermission.MEMBER); // needed?
        settingsPacket.setCommandPermission(CommandPermission.NORMAL); // needed?

        bedrockConnection.sendPacket(settingsPacket);
    }
}
