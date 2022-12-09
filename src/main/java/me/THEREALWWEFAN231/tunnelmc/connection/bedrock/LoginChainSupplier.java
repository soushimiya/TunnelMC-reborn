package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.utils.JoseUtils;

import java.security.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class LoginChainSupplier implements Supplier<CompletableFuture<ChainData>> {

    protected byte[] signBytes(PrivateKey key, byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA384withECDSA");
        signature.initSign(key);
        signature.update(dataToSign);

        return JoseUtils.convertDERToJOSE(signature.sign(), JoseUtils.AlgorithmType.ECDSA384);
    }
}
