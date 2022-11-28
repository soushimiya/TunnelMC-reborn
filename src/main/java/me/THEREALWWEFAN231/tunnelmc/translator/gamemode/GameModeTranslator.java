package me.THEREALWWEFAN231.tunnelmc.translator.gamemode;

import com.nukkitx.protocol.bedrock.data.GameType;

import net.minecraft.world.GameMode;

public class GameModeTranslator {

	public static GameMode bedrockToJava(GameType gameType) {
		switch (gameType) {
			case SURVIVAL:
			case SURVIVAL_VIEWER:
			case DEFAULT:
				return GameMode.SURVIVAL;
			case CREATIVE:
			case CREATIVE_VIEWER:
			case SPECTATOR:
				return GameMode.CREATIVE;
			case ADVENTURE:
				return GameMode.ADVENTURE;
		}

		return GameMode.DEFAULT;
	}

}
