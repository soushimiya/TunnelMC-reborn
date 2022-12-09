package me.THEREALWWEFAN231.tunnelmc.events.slot;

import lombok.Getter;
import net.minecraft.screen.ScreenHandler;

@Getter
public class DropSlotEvent extends SlotEvent {
    private final int button;

    public DropSlotEvent(ScreenHandler screenHandler, int slotIndex, int button) {
        super(screenHandler, slotIndex);
        this.button = button;
    }
}
