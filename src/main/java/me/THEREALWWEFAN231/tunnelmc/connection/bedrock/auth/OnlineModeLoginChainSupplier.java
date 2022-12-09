package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth;

import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.LoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.XboxToken;

import java.io.OutputStream;
import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class OnlineModeLoginChainSupplier implements LoginChainSupplier {
    private final OutputStream stream;

    public CompletableFuture<ChainData> get() {
        KeyPair keyPair = EncryptionUtils.createKeyPair();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

        return LiveAuthorization.getAccessToken(stream).thenApply(accessToken -> {
            XboxToken xboxToken = XboxAuthorization.getXBLToken(accessToken, "https://multiplayer.minecraft.net/");
            return MinecraftAuthentication.getMinecraftChain(publicKey, xboxToken);
        }).thenApply(chain -> new ChainData(chain, keyPair));
    }
}
