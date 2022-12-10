package me.THEREALWWEFAN231.tunnelmc.events;

import com.nukkitx.api.event.Event;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class SessionInitializedEvent implements Event {
    private final BedrockClientSession session;
}
