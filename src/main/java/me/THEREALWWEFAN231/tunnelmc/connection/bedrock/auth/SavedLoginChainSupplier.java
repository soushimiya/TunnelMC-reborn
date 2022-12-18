package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.github.scribejava.core.model.OAuth2AccessToken;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

public class SavedLoginChainSupplier extends OnlineModeLoginChainSupplier {

    public SavedLoginChainSupplier(File rememberAccountFile) {
        super(rememberAccountFile);
    }

    public CompletableFuture<ChainData> get() {
        if(this.rememberAccountFile == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("No file specified"));
        }

        try {
            return CompletableFuture.completedFuture(getChain(LiveAuthorization.INSTANCE.getRefreshedToken(
                    JSON_MAPPER.readValue(this.rememberAccountFile, OAuth2AccessToken.class))));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
