package me.THEREALWWEFAN231.tunnelmc.events.slot;

import lombok.Getter;
import net.minecraft.screen.ScreenHandler;

@Getter
public class QuickMoveSlotEvent extends SlotEvent {
    public QuickMoveSlotEvent(ScreenHandler screenHandler, int slotIndex) {
        super(screenHandler, slotIndex);
    }
}
