package me.THEREALWWEFAN231.tunnelmc.events;

import com.nukkitx.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PlayerTickEvent implements Event {
    private final long tick;
}
