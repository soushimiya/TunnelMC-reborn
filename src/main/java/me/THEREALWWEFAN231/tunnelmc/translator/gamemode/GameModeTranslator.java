package me.THEREALWWEFAN231.tunnelmc.translator.gamemode;

import com.nukkitx.protocol.bedrock.data.GameType;

import net.minecraft.world.GameMode;

public class GameModeTranslator {

	public static GameMode bedrockToJava(GameType gameType) {
		return switch (gameType) {
			case SURVIVAL, SURVIVAL_VIEWER, DEFAULT -> GameMode.SURVIVAL;
			case CREATIVE, CREATIVE_VIEWER, SPECTATOR -> GameMode.CREATIVE;
			case ADVENTURE -> GameMode.ADVENTURE;
		};
	}
}
