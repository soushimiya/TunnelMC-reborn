package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.block.BlockState;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Used for server implementations that use an older chunk encoding version that use a different block palette.
 */
public final class LegacyBlockPaletteManager {
    public static final Map<Integer, BlockState> LEGACY_BLOCK_TO_JAVA_ID;

    static {
        Map<Integer, BlockState> map = new Int2ObjectOpenHashMap<>();
        try {
            JsonNode blockIdMap = FileUtils.getJsonFromResource("pmmp/block_id_map.json");
            ArrayNode blockStateMetaMap = (ArrayNode) FileUtils.getJsonFromResource("pmmp/block_state_meta_map.json");

            int blockRuntimeId = 0;
            for (JsonNode metaNode : blockStateMetaMap) {
                int runtimeId = blockRuntimeId++;

                TunnelBlockState tunnelBlockState = BlockPaletteTranslator.RUNTIME_ID_TO_BEDROCK_BLOCK_STATE.get(runtimeId);
                String name = tunnelBlockState.toString(false);
                int id = blockIdMap.get(name).asInt();
                int meta = metaNode.asInt();

                int legacyId = id << 6 | meta;
                map.put(legacyId, BlockStateTranslator.getBlockStateFromRuntimeId(runtimeId));
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to create legacy mapping", e);
        }
        LEGACY_BLOCK_TO_JAVA_ID = Collections.unmodifiableMap(map);
    }
}
