package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.OutputStream;

public class LiveOAuth20Service extends OAuth20Service {
    public LiveOAuth20Service(DefaultApi20 api, String apiKey, String apiSecret, String callback, String defaultScope, String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }

    @Override
    protected OAuthRequest createDeviceAuthorizationCodesRequest(String scope) {
        OAuthRequest request = super.createDeviceAuthorizationCodesRequest(scope);
        if(getResponseType() != null) {
            request.addParameter(OAuthConstants.RESPONSE_TYPE, getResponseType());
        }

        return request;
    }
}
