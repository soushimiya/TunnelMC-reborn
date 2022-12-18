package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.OAuth2Error;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class AccessTokenTask implements Supplier<OAuth2AccessToken> {
    private final OAuth20Service service;
    private final DeviceAuthorization authorization;
    @Getter
    private long intervalMillis;
    private long nextExecution;

    public AccessTokenTask(OAuth20Service service, DeviceAuthorization authorization) {
        this.service = service;
        this.authorization = authorization;
        this.intervalMillis = authorization.getIntervalSeconds() * 1000L;
    }

    @Override
    public OAuth2AccessToken get() {
        this.nextExecution = System.currentTimeMillis() + this.intervalMillis;
        try {
            return this.service.getAccessTokenDeviceAuthorizationGrant(authorization);
        } catch (OAuth2AccessTokenErrorResponse e) {
            if (e.getError() != OAuth2Error.AUTHORIZATION_PENDING) {
                if (e.getError() == OAuth2Error.SLOW_DOWN) {
                    this.intervalMillis += 5000;
                } else {
                    throw e;
                }
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean canExecute() {
        return System.currentTimeMillis() > this.nextExecution;
    }
}
