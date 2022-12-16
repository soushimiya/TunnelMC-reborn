package me.THEREALWWEFAN231.tunnelmc.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.IOException;

public class OAuth2AccessTokenDeserializer extends StdDeserializer<OAuth2AccessToken> {
    public OAuth2AccessTokenDeserializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public OAuth2AccessToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String tokenType = node.get("tokenType").asText();
        String accessToken = node.get("accessToken").asText();
        String refreshToken = node.get("refreshToken").asText();
        String scope = node.get("scope").asText();
        int expiresIn = node.get("expiresIn").numberValue().intValue();

        return new OAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope, null);
    }
}
