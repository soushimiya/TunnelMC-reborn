package me.THEREALWWEFAN231.tunnelmc.translator;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;

public abstract class TranslatorManager<T extends Translator<?, ?>, P> {

	protected abstract void addTranslator(T translator);
	public abstract void translateData(P data, BedrockConnection bedrockConnection, FakeJavaConnection connection);
}
