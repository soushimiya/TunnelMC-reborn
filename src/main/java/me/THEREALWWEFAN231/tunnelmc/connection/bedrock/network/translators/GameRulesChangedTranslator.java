package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.data.GameRuleData;
import com.nukkitx.protocol.bedrock.packet.GameRulesChangedPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.world.GameRules;

import java.util.List;

@PacketIdentifier(GameRulesChangedPacket.class)
public class GameRulesChangedTranslator extends PacketTranslator<GameRulesChangedPacket> {

    @Override
    public void translate(GameRulesChangedPacket packet, Client client) {
        if (TunnelMC.mc.world == null) {
            TunnelMC.mc.execute(() -> onGameRulesChanged(packet.getGameRules()));
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
