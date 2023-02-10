package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.io.IOException;
import java.util.*;

@Log4j2
public class BlockStateTranslator {
	//TODO: create an override file, which allows us to override the blocks.json information and or create new information, once we do that fix all the printlns' from BlockPaletteTranslator
	//TODO: i also dont think water logged blocks work, i cant test right now as i cant connect to a dedicated bedrock server

	public static final Map<TunnelBlockState, TunnelBlockState> JAVA_TO_BEDROCK;
	public static final Map<TunnelBlockState, TunnelBlockState> BEDROCK_TO_JAVA;

	public static final Map<Integer, BlockState> RUNTIME_ID_TO_BLOCK_STATE;
	public static final Map<BlockState, Integer> BLOCK_STATE_TO_RUNTIME_ID;

	static {
		Map<TunnelBlockState, TunnelBlockState> javaBedrock = new HashMap<>();
		Map<TunnelBlockState, TunnelBlockState> bedrockJava = new HashMap<>();
		Map<Integer, BlockState> runtimeIdBlockState = new HashMap<>();
		Map<BlockState, Integer> blockStateRuntimeId = new HashMap<>();

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

				if (javaBlockState.getVanillaBlock() == null) {
					log.debug("j2b: Cannot find java block state: {}, Skipping!", javaBlockState);
					continue;
				}
				Integer runtimeId = BlockPaletteTranslator.getBedrockBlockId(bedrockBlockState);
				if(runtimeId == null) {
					log.debug("j2b: Cannot find bedrock block state's runtime id: {}, Skipping!", bedrockBlockState);
					continue;
				}
				blockStateRuntimeId.put(javaBlockState.getBlockState(), runtimeId);
				javaBedrock.put(javaBlockState, bedrockBlockState);
			}
			for (Iterator<Map.Entry<String, JsonNode>> it = b2j.fields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromString(entry.getKey());
				TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(entry.getValue().asText());

				if (javaBlockState.getVanillaBlock() == null) {
					log.debug("b2j: Cannot find java block state: {}, Skipping!", javaBlockState);
					continue;
				}
				Integer runtimeId = BlockPaletteTranslator.getBedrockBlockId(bedrockBlockState);
				if(runtimeId == null) {
					log.debug("b2j: Cannot find bedrock block state's runtime id: {}, Skipping!", bedrockBlockState);
					continue;
				}
				runtimeIdBlockState.put(runtimeId, javaBlockState.getBlockState());
				bedrockJava.put(bedrockBlockState, javaBlockState);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		JAVA_TO_BEDROCK = Collections.unmodifiableMap(javaBedrock);
		BEDROCK_TO_JAVA = Collections.unmodifiableMap(bedrockJava);

		RUNTIME_ID_TO_BLOCK_STATE = Collections.unmodifiableMap(runtimeIdBlockState);
		BLOCK_STATE_TO_RUNTIME_ID = Collections.unmodifiableMap(blockStateRuntimeId);
	}

	public static Integer getRuntimeIdFromBlockState(BlockState state) {
		Integer runtimeId = BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(state, null);
		if(runtimeId == null) {
			runtimeId = Optional.ofNullable(BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(state.getBlock().getDefaultState(), null))
					.orElse(BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(Blocks.STONE.getDefaultState(), null));
		}

		return runtimeId;
	}

	public static BlockState getBlockStateFromRuntimeId(int runtimeId) {
		BlockState state = RUNTIME_ID_TO_BLOCK_STATE.getOrDefault(runtimeId, null);
		if(state == null) {
			TunnelBlockState tunnelBlockState = BlockPaletteTranslator.RUNTIME_ID_TO_BEDROCK_BLOCK_STATE.get(runtimeId);
			state = Optional.ofNullable(BlockStateTranslator.BEDROCK_TO_JAVA.get(tunnelBlockState))
					.map(TunnelBlockState::getBlockState)
					.orElse(Blocks.STONE.getDefaultState());
		}

		return state;
	}

	public static <T> T getFromMap(Map<T, T> map, T find, T defaultValue) {
		for (T entry : map.keySet()) {
			if(entry.equals(find)) {
				return map.getOrDefault(entry, defaultValue);
			}
		}

		return null;
	}

	public static <T> T getFromMap(Map<T, T> map, T find) {
		return getFromMap(map, find, null);
	}

//	static {
//		Map<TunnelBlockState, TunnelBlockState> javaBedrock = new HashMap<>();
//		Map<TunnelBlockState, TunnelBlockState> bedrockJava = new HashMap<>();
//
//		try {
//			ObjectNode blocks = (ObjectNode) FileUtils.getJsonFromResource("geyser/blocks.json");
//
//			Iterator<String> fieldNames = blocks.fieldNames();
//			while (fieldNames.hasNext()) {
//				String javaBlock = fieldNames.next();
//				TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(javaBlock);
//				if (javaBlockState.getVanillaBlock() == null) {
//					log.debug("Cannot find java block state: {}, Skipping!", javaBlockState);
//					continue;
//				}
//
//				JsonNode bedrockBlock = blocks.get(javaBlock);
//				String bedrockBlockName = bedrockBlock.get("bedrock_identifier").asText();
//				TunnelBlockState bedrockBlockState = new TunnelBlockState(bedrockBlockName, getStatesFromJson(bedrockBlock.get("bedrock_states")));
//
//				javaBedrock.put(javaBlockState, bedrockBlockState);
//				if(!bedrockJava.containsKey(bedrockBlockState)) {
//					bedrockJava.put(bedrockBlockState, javaBlockState);
//				}
//
//				Integer runtimeId = BlockPaletteTranslator.getBedrockBlockId(bedrockBlockState);
//				if(runtimeId != null) {
//					blockStateRuntimeId.put(javaBlockState.getBlockState(), runtimeId);
//					if(!runtimeIdBlockState.containsKey(runtimeId)) {
//						runtimeIdBlockState.put(runtimeId, javaBlockState.getBlockState());
//					}
//					continue;
//				}
//
//				throw new RuntimeException("Unable to find suitable bedrock block state with runtime id: " + javaBlockState);
//			}
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		JAVA_TO_BEDROCK = Collections.unmodifiableMap(javaBedrock);
//		BEDROCK_TO_JAVA = Collections.unmodifiableMap(bedrockJava);
//	}
//
//	private static Map<String, String> getStatesFromJson(JsonNode states) {
//		if(states == null) {
//			return Map.of();
//		}
//
//		return JSON_MAPPER.convertValue(states, new TypeReference<>() {});
//	}
}
