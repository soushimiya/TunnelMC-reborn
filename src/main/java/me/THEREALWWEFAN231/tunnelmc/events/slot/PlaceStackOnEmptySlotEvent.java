package me.THEREALWWEFAN231.tunnelmc.events.slot;

import lombok.Getter;
import net.minecraft.screen.ScreenHandler;

@Getter
public class PlaceStackOnEmptySlotEvent extends SlotEvent {
    private final int count;

    public PlaceStackOnEmptySlotEvent(ScreenHandler screenHandler, int slotIndex, int count) {
        super(screenHandler, slotIndex);
        this.count = count;
    }
}
