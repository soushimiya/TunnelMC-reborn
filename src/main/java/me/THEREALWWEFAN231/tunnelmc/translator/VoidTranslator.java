package me.THEREALWWEFAN231.tunnelmc.translator;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

public interface VoidTranslator<T> extends Translator<T, Void> {

    default Void translateType(T data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        translate(data, bedrockConnection, javaConnection);
        return null;
    }

    void translate(T data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection);
}
