package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface LoginChainSupplier extends Supplier<CompletableFuture<ChainData>> {
}
