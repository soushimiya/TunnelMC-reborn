package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/*
 * as of 1.16.100, the block palette is static between all servers, so we can load this once and be over it
 * it uses BlockStateTranslator which loaded blocks.json, from BlockStateTranslator we can match blocks and get their runtime id for a Bedrock server
 */
@Log4j2
public class BlockPaletteTranslator {

	public static int AIR_BEDROCK_BLOCK_ID;
	public static int WATER_BEDROCK_BLOCK_ID;

	// Used for persistent v8 decoding.
	public static final Map<Integer, TunnelBlockState> RUNTIME_ID_TO_BEDROCK_BLOCK_STATE;
	public static final Map<TunnelBlockState, Integer> BEDROCK_BLOCK_STATE_TO_RUNTIME_ID;

	static {
		InputStream stream = FileUtils.class.getClassLoader().getResourceAsStream("geyser/block_palette.nbt");
		if (stream == null) {
			throw new RuntimeException("Could not find the block palette file!");
		}

		Map<Integer, TunnelBlockState> runtimeIdBedrockBlockState = new Int2ObjectOpenHashMap<>();
		Map<TunnelBlockState, Integer> bedrockBlockStateRuntimeId = new Object2IntOpenHashMap<>();
		try (NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(new GZIPInputStream(stream)))) {
			NbtMap blockPalette = (NbtMap) nbtInputStream.readTag();
			NbtList<NbtMap> blocksTag = (NbtList<NbtMap>) blockPalette.getList("blocks", NbtType.COMPOUND);

			int runtimeId = 0;
			for (NbtMap nbtMap : blocksTag) {
				TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromNBTMap(nbtMap);
				runtimeIdBedrockBlockState.put(runtimeId, bedrockBlockState);
				bedrockBlockStateRuntimeId.put(bedrockBlockState, runtimeId);
				runtimeId++;

				if (bedrockBlockState.getIdentifier().equals("minecraft:air")) {
					AIR_BEDROCK_BLOCK_ID = runtimeId;
				} else if (bedrockBlockState.getIdentifier().equals("minecraft:water")) {
					WATER_BEDROCK_BLOCK_ID = runtimeId;
				}
			}
		} catch (Exception e) {
			throw new AssertionError("Unable to get blocks from runtime block states", e);
		}

		RUNTIME_ID_TO_BEDROCK_BLOCK_STATE = Collections.unmodifiableMap(runtimeIdBedrockBlockState);
		BEDROCK_BLOCK_STATE_TO_RUNTIME_ID = Collections.unmodifiableMap(bedrockBlockStateRuntimeId);
	}

	public static Integer getBedrockBlockId(TunnelBlockState state) {
		for (TunnelBlockState entry : BlockPaletteTranslator.BEDROCK_BLOCK_STATE_TO_RUNTIME_ID.keySet()) {
			if(entry.equals(state)) {
				return BlockPaletteTranslator.BEDROCK_BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(entry, null);
			}
		}

		return null;
	}
}
