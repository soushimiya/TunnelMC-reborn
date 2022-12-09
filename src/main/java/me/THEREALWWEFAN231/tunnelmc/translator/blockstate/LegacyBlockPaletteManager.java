package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.nukkitx.nbt.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.block.BlockState;

import java.io.IOException;
import java.io.InputStream;

/**
 * Used for server implementations that use an older chunk encoding version that use a different block palette.
 */
public final class LegacyBlockPaletteManager {
    public static final Int2ObjectMap<BlockState> LEGACY_BLOCK_TO_JAVA_ID = new Int2ObjectOpenHashMap<>();

    static {
        NbtList<NbtMap> legacyBlockStates;
        try (InputStream stream = FileUtils.class.getClassLoader().getResourceAsStream("tunnel/runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state tag!");
            }
            try (NBTInputStream nbtStream = NbtUtils.createGZIPReader(stream)) {
                //noinspection unchecked
                legacyBlockStates = (NbtList<NbtMap>) nbtStream.readTag();
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to load block palette", e);
        }

//        int bedrockRuntimeId = -1;
        for (NbtMap nbt : legacyBlockStates) {
//            bedrockRuntimeId++;
            if (nbt.get("id") == null || nbt.get("data") == null || nbt.get("runtimeId") == null) {
                throw new AssertionError("Unable to map block palette");
            }

            int legacyId = nbt.getInt("id") << 6 | nbt.getShort("data");
            LEGACY_BLOCK_TO_JAVA_ID.put(legacyId, BlockPaletteTranslator.RUNTIME_ID_TO_BLOCK_STATE.get(nbt.getInt("runtimeId")));
        }
    }
}
