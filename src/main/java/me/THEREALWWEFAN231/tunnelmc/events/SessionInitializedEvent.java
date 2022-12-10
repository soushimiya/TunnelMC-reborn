package me.THEREALWWEFAN231.tunnelmc.events;

import com.nukkitx.api.event.Event;
import com.nukkitx.protocol.bedrock.BedrockClientSession;

public record SessionInitializedEvent(BedrockClientSession session) implements Event {
}
