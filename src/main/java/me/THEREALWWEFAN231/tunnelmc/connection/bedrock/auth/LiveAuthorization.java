package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.nukkitx.api.event.Listener;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.oauth.ExtendedLiveApi;
import me.THEREALWWEFAN231.tunnelmc.events.GameTickEvent;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class LiveAuthorization {
    public static final LiveAuthorization INSTANCE = new LiveAuthorization();
    private static final OAuth20Service SERVICE = new ServiceBuilder("0000000048183522")
            .responseType("device_code")
            .defaultScope("service::user.auth.xboxlive.com::MBI_SSL")
            .build(ExtendedLiveApi.instance());

    private final Map<AccessTokenTask, Consumer<OAuth2AccessToken>> tasks = new HashMap<>();

    private LiveAuthorization() {
        TunnelMC.getInstance().getEventManager().registerListeners(this, this);
    }

    public String getAccessToken(Consumer<OAuth2AccessToken> callback) {
        try {
            DeviceAuthorization authorization = SERVICE.getDeviceAuthorizationCodes();
            tasks.put(new AccessTokenTask(SERVICE, authorization), callback);

            return "Authenticate at " + authorization.getVerificationUri() + " with code " + authorization.getUserCode();
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
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

    @Listener
    public void onEvent(GameTickEvent event) {
        for (AccessTokenTask task : tasks.keySet()) {
            if(!task.canExecute()) {
                continue;
            }

            OAuth2AccessToken accessToken = task.get();
            if(accessToken == null) {
                continue;
            }

            tasks.get(task).accept(accessToken);
            tasks.remove(task);
            return;
        }
    }
}
