package me.THEREALWWEFAN231.tunnelmc.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.IOException;

public class OAuth2AccessTokenSerializer extends StdSerializer<OAuth2AccessToken> {
    public OAuth2AccessTokenSerializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public void serialize(OAuth2AccessToken value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("tokenType", value.getTokenType());
        gen.writeStringField("accessToken", value.getAccessToken());
        gen.writeStringField("refreshToken", value.getRefreshToken());
        gen.writeStringField("scope", value.getScope());
        gen.writeNumberField("expiresIn", value.getExpiresIn());
        gen.writeEndObject();
    }
}
