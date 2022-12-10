package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.SetPlayerGameTypePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;

@PacketIdentifier(SetPlayerGameTypePacket.class)
public class SetPlayerGameTypeTranslator extends PacketTranslator<SetPlayerGameTypePacket> {

    @Override
    public void translate(SetPlayerGameTypePacket packet, BedrockConnection bedrockConnection) {
        GameMode javaGameMode;
        switch (packet.getGamemode()) {
            case 0 -> javaGameMode = GameMode.SURVIVAL;
            case 1 -> javaGameMode = GameMode.CREATIVE;
            case 2 -> javaGameMode = GameMode.ADVENTURE;
            default -> {
                System.out.println("Couldn't find the Java game mode for: " + packet.getGamemode());
                return;
            }
        }

        bedrockConnection.javaConnection.processServerToClientPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.GAME_MODE_CHANGED,
                (float) javaGameMode.getId()));
    }
}
