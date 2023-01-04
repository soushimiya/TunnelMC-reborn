package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.screen.ScreenHandler;

public class DynamicContainer extends GenericContainer {
    public DynamicContainer(int size) {
        super(size);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void updateInventory() {
        ScreenHandler screenHandler = TunnelMC.mc.player.currentScreenHandler;
        for (int i = 0; i < this.getSize(); i++) {
            Integer javaSlotId = ScreenHandlerTranslatorManager.getJavaSlotFromBedrockContainer(screenHandler, this, i);
            if (javaSlotId == null) {
                break;
            }

            screenHandler.getSlot(javaSlotId).setStackNoCallbacks(ItemTranslator.itemDataToItemStack(this.getItemFromSlot(i)));
        }
    }
}
