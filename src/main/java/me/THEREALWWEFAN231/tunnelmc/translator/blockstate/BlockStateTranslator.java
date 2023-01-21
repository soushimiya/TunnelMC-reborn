package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public class BlockStateTranslator {
	//TODO: create an override file, which allows us to override the blocks.json information and or create new information, once we do that fix all the printlns' from BlockPaletteTranslator
	//TODO: i also dont think water logged blocks work, i cant test right now as i cant connect to a dedicated bedrock server

	public static final Map<TunnelBlockState, TunnelBlockState> JAVA_TO_BEDROCK;
	public static final Map<TunnelBlockState, TunnelBlockState> BEDROCK_TO_JAVA;

	static {
		Map<TunnelBlockState, TunnelBlockState> javaBedrock = new HashMap<>();
		Map<TunnelBlockState, TunnelBlockState> bedrockJava = new HashMap<>();

		try {
			ObjectNode j2b = (ObjectNode) FileUtils.getJsonFromResource("prismarinejs/blocksJ2B.json");
			ObjectNode b2j = (ObjectNode) FileUtils.getJsonFromResource("prismarinejs/blocksB2J.json");
			if(j2b == null || b2j == null) {
				throw new NullPointerException("Cannot find/use blockstate translator files");
			}

			for (Iterator<Map.Entry<String, JsonNode>> it = j2b.fields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(entry.getKey());
				TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromString(entry.getValue().asText());

				javaBedrock.put(javaBlockState, bedrockBlockState);
			}
			for (Iterator<Map.Entry<String, JsonNode>> it = b2j.fields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromString(entry.getKey());
				TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(entry.getValue().asText());

				bedrockJava.put(bedrockBlockState, javaBlockState);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		JAVA_TO_BEDROCK = Collections.unmodifiableMap(javaBedrock);
		BEDROCK_TO_JAVA = Collections.unmodifiableMap(bedrockJava);
	}

	public static <T> T getFromMap(Map<T, T> map, T find) {
		for (T entry : map.keySet()) {
			if(entry.equals(find)) {
				return map.getOrDefault(entry, null);
			}
		}

		return null;
	}
}
