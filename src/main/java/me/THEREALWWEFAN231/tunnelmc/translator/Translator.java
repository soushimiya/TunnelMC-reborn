package me.THEREALWWEFAN231.tunnelmc.translator;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

@FunctionalInterface
public interface Translator<T, R> {

    R translateType(T data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection);
}
