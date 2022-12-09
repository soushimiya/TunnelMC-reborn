package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AuthData(@JsonProperty("XUID") String xuid,
                       String displayName,
                       UUID identity,
                       @JsonInclude(JsonInclude.Include.NON_ABSENT) String titleId) {

    public static AuthData offlineMode(String displayName, UUID identity) {
        return new AuthData("", displayName, identity, null);
    }
}
