package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.oauth;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

import java.io.OutputStream;

public class ExtendedLiveApi extends LiveApi {
    protected ExtendedLiveApi() {
    }

    private static class InstanceHolder {
        private static final ExtendedLiveApi INSTANCE = new ExtendedLiveApi();
    }

    public static ExtendedLiveApi instance() {
        return ExtendedLiveApi.InstanceHolder.INSTANCE;
    }

    @Override
    public String getDeviceAuthorizationEndpoint() {
        return "https://login.live.com/oauth20_connect.srf";
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }

    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope, String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new LiveOAuth20Service(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }
}
