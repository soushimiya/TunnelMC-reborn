package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.UpdatePlayerGameTypePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.gamemode.GameModeTranslator;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;

@PacketIdentifier(UpdatePlayerGameTypePacket.class)
public class UpdatePlayerGameTypeTranslator extends PacketTranslator<UpdatePlayerGameTypePacket> {

    @Override
    public void translate(UpdatePlayerGameTypePacket packet, Client client) {
        GameMode javaGameMode = GameModeTranslator.bedrockToJava(packet.getGameType());

        client.javaConnection.processServerToClientPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.GAME_MODE_CHANGED,
                (float) javaGameMode.getId()));
    }
}
