package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.experimental.UtilityClass;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.oauth.ExtendedLiveApi;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@UtilityClass
public class LiveAuthorization {

    public CompletableFuture<OAuth2AccessToken> getAccessToken(OutputStream outputStream) {
        final OAuth20Service service = new ServiceBuilder("0000000048183522")
                .responseType("device_code")
                .defaultScope("service::user.auth.xboxlive.com::MBI_SSL")
                .build(ExtendedLiveApi.instance());

        CompletableFuture<OAuth2AccessToken> future = new CompletableFuture<>();
        try {
            DeviceAuthorization authorization = service.getDeviceAuthorizationCodes();
            outputStream.write(("Authenticate at " + authorization.getVerificationUri() + " with code " + authorization.getUserCode() + "\n").getBytes(StandardCharsets.UTF_8));

            new Thread(() -> {
                try {
                    future.complete(service.pollAccessTokenDeviceAuthorizationGrant(authorization));
                } catch (InterruptedException | ExecutionException | IOException e) {
                    future.completeExceptionally(e);
                }
            }).start();
        } catch (InterruptedException | ExecutionException | IOException e) {
            future.completeExceptionally(e);
        }

        return future;
    }
}
