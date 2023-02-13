package me.THEREALWWEFAN231.tunnelmc.events;

import com.nukkitx.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;

@Getter
@RequiredArgsConstructor
public final class SessionClosedEvent implements Event {
    private final BedrockConnection bedrockConnection;
}
