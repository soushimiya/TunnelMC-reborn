package me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.translators;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers.PlayerInventoryContainer;
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
	public int getJavaSlotId(BedrockContainer bedrockContainer, int bedrockSlotId) {
		if (bedrockContainer instanceof PlayerInventoryContainer) {
			if (bedrockSlotId < 9) {//convert bedrock hotbar slots to java hotbar slots
				return 36 + bedrockSlotId;
			}
		}

		return super.getJavaSlotId(bedrockContainer, bedrockSlotId);
	}

	@Override
	public int getBedrockSlotId(PlayerScreenHandler javaContainer, int javaSlotId) {
		if (javaSlotId >= 5 && javaSlotId <= 8) {//armor slots
			return javaSlotId - 5;//convert to bedrock container slots, 0-3
		} else if (javaSlotId >= 9 && javaSlotId <= 44) {//java main inventory slot ids

			if (javaSlotId >= 36) {//if it's a java hotbar slot
				return javaSlotId - 36;//convert it to a bedrock hotbar slot 0-8
			}
			return javaSlotId;//the rest(27 inventory slots) have the same ids on java and bedrock
		} else if (javaSlotId == 45) {//java offhand slot id
			return 0;
		}

		return 0;
	}

	@Override
	public Class<? extends ScreenHandler> getScreenHandlerClass() {
		return PlayerScreenHandler.class;
	}

}
