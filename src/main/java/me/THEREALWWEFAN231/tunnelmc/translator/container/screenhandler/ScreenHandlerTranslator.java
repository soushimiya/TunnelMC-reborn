package me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler;

import net.minecraft.screen.ScreenHandler;

public abstract class ScreenHandlerTranslator<T extends ScreenHandler> {
	
	public abstract Integer getBedrockContainerId(T javaContainer, int javaSlotId);
	
	public abstract Class<? extends ScreenHandler> getScreenHandlerClass();//i could use reflection but it generally wouldn't be ideal
}
