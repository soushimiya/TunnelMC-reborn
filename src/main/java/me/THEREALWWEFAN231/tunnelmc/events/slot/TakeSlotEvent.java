package me.THEREALWWEFAN231.tunnelmc.events.slot;

import lombok.Getter;
import net.minecraft.screen.ScreenHandler;

@Getter
public class TakeSlotEvent extends SlotEvent {
    public TakeSlotEvent(ScreenHandler screenHandler, int slotIndex) {
        super(screenHandler, slotIndex);
    }
}
