package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.experimental.UtilityClass;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.oauth.ExtendedLiveApi;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@UtilityClass
public class LiveAuthorization {
    private static final OAuth20Service SERVICE = new ServiceBuilder("0000000048183522")
            .responseType("device_code")
            .defaultScope("service::user.auth.xboxlive.com::MBI_SSL")
            .build(ExtendedLiveApi.instance());

    public CompletableFuture<OAuth2AccessToken> getAccessToken(Consumer<String> callback) {
        CompletableFuture<OAuth2AccessToken> future = new CompletableFuture<>();
        try {
            DeviceAuthorization authorization = SERVICE.getDeviceAuthorizationCodes();
            callback.accept("Authenticate at " + authorization.getVerificationUri() + " with code " + authorization.getUserCode());

            new Thread(() -> {
                try {
                    future.complete(SERVICE.pollAccessTokenDeviceAuthorizationGrant(authorization));
                } catch (InterruptedException | ExecutionException | IOException e) {
                    future.completeExceptionally(e);
                }
            }).start();
        } catch (InterruptedException | ExecutionException | IOException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    public OAuth2AccessToken getRefreshedToken(OAuth2AccessToken token) {
        if(!Instant.now().isAfter(Instant.now().plus(token.getExpiresIn(), ChronoUnit.SECONDS))) {
            return token;
        }

        try {
            return SERVICE.refreshAccessToken(token.getRefreshToken());
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
