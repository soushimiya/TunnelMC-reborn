package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.SetPlayerGameTypePacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;

@Log4j2
@PacketIdentifier(SetPlayerGameTypePacket.class)
public class SetPlayerGameTypeTranslator extends PacketTranslator<SetPlayerGameTypePacket> {

    @Override
    public void translate(SetPlayerGameTypePacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        GameMode javaGameMode;
        switch (packet.getGamemode()) {
            case 0 -> javaGameMode = GameMode.SURVIVAL;
            case 1 -> javaGameMode = GameMode.CREATIVE;
            case 2 -> javaGameMode = GameMode.ADVENTURE;
            default -> {
                log.error("Couldn't find the correct gamemode for: " + packet);
                return;
            }
        }

        javaConnection.processJavaPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.GAME_MODE_CHANGED,
                (float) javaGameMode.getId()));
    }
}
