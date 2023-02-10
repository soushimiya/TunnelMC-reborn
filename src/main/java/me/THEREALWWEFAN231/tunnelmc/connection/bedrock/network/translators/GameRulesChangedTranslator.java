package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.data.GameRuleData;
import com.nukkitx.protocol.bedrock.packet.GameRulesChangedPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.world.GameRules;

import java.util.List;

@PacketIdentifier(GameRulesChangedPacket.class)
public class GameRulesChangedTranslator extends PacketTranslator<GameRulesChangedPacket> {

    @Override
    public void translate(GameRulesChangedPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (TunnelMC.mc.world == null) {
            TunnelMC.mc.executeSync(() -> onGameRulesChanged(packet.getGameRules()));
        } else {
            onGameRulesChanged(packet.getGameRules());
        }
    }

    public static void onGameRulesChanged(List<GameRuleData<?>> gamerules) { // TODO: not here
        if (TunnelMC.mc.world == null || TunnelMC.mc.player == null) {
            return;
        }

        for (GameRuleData<?> gameRule : gamerules) {
            switch (gameRule.getName()) {
                case "dodaylightcycle" -> TunnelMC.mc.world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(((Boolean) gameRule.getValue()), null);
                case "doimmediaterespawn" -> TunnelMC.mc.player.setShowsDeathScreen(!((Boolean) gameRule.getValue()));
            }
        }
    }
}
