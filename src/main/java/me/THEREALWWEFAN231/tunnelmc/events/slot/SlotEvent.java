package me.THEREALWWEFAN231.tunnelmc.events.slot;

import com.nukkitx.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.screen.ScreenHandler;

@Getter
@RequiredArgsConstructor
public abstract class SlotEvent implements Event {
    private final ScreenHandler screenHandler;
    private final int slotIndex;
}
