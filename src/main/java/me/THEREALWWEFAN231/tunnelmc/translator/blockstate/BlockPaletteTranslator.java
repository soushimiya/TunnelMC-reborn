package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

/*
 * as of 1.16.100, the block palette is static between all servers, so we can load this once and be over it
 * it uses BlockStateTranslator which loaded blocks.json, from BlockStateTranslator we can match blocks and get their runtime id for a Bedrock server
 */
@Log4j2
public class BlockPaletteTranslator {

	public static int AIR_BEDROCK_BLOCK_ID;
	public static int WATER_BEDROCK_BLOCK_ID;

	// Used for persistent v8 decoding.
	private static final Object2IntMap<String> BEDROCK_BLOCK_STATE_TO_RUNTIME_ID = new Object2IntOpenHashMap<>();

	public static final Int2ObjectMap<BlockState> RUNTIME_ID_TO_BLOCK_STATE = new Int2ObjectOpenHashMap<>();
	public static final Object2IntMap<BlockState> BLOCK_STATE_TO_RUNTIME_ID = new Object2IntOpenHashMap<>();

	public static void loadMap(NbtList<NbtMap> blockPaletteData) {
		int runtimeId = 0;
		for (NbtMap nbtMap : blockPaletteData) {
			TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromNBTMap(nbtMap);
			BEDROCK_BLOCK_STATE_TO_RUNTIME_ID.put(bedrockBlockState.toString(), runtimeId);

			TunnelBlockState javaBlockState = BlockStateTranslator.BEDROCK_TO_JAVA.get(bedrockBlockState);
			if (javaBlockState.equals(bedrockBlockState)) {
				RUNTIME_ID_TO_BLOCK_STATE.put(runtimeId, javaBlockState.getBlockState());
				BLOCK_STATE_TO_RUNTIME_ID.put(javaBlockState.getBlockState(), runtimeId);
				if (bedrockBlockState.getIdentifier().equals("minecraft:air")) {
					AIR_BEDROCK_BLOCK_ID = runtimeId;
				} else if (bedrockBlockState.getIdentifier().equals("minecraft:water")) {
					WATER_BEDROCK_BLOCK_ID = runtimeId;
				}
			} else {
				log.debug("Unable to find suitable block state for " + bedrockBlockState);
				RUNTIME_ID_TO_BLOCK_STATE.put(runtimeId, Blocks.STONE.getDefaultState());//we could probably put the default state, but for now we will use stone
			}

			runtimeId++;
		}

	}

	public static int getBedrockBlockId(TunnelBlockState state) {
		return BlockPaletteTranslator.BEDROCK_BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(state.toString(), AIR_BEDROCK_BLOCK_ID);
	}
}
