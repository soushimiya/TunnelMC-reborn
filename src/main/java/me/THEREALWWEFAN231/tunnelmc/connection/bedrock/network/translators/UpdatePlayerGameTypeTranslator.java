package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.UpdatePlayerGameTypePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.gamemode.GameModeTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;

@PacketIdentifier(UpdatePlayerGameTypePacket.class)
public class UpdatePlayerGameTypeTranslator extends PacketTranslator<UpdatePlayerGameTypePacket> {

    @Override
    public void translate(UpdatePlayerGameTypePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        GameMode javaGameMode = GameModeTranslator.bedrockToJava(packet.getGameType());

        javaConnection.processJavaPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.GAME_MODE_CHANGED,
                (float) javaGameMode.getId()));
    }
}
