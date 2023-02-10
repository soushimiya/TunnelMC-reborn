package me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.translators;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslator;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class PlayerScreenHandlerTranslator extends ScreenHandlerTranslator<PlayerScreenHandler> {

	@Override
	public Integer getBedrockContainerId(PlayerScreenHandler javaContainer, int javaSlotId) {
		if (javaSlotId >= 9 && javaSlotId <= 44) {//java main inventory slot ids
			return BedrockContainers.PLAYER_INVENTORY_COTNAINER_ID;
		} else if (javaSlotId >= 5 && javaSlotId <= 8) {//java armor slot ids
			return BedrockContainers.PLAYER_ARMOR_COTNAINER_ID;
		} else if (javaSlotId == 45) {//java offhand slot id
			return BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID;
		}

		return null;
	}

	@Override
	public Class<? extends ScreenHandler> getScreenHandlerClass() {
		return PlayerScreenHandler.class;
	}
}
