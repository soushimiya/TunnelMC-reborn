package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.util.stream.NetworkDataInputStream;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.bitarray.BitArray;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.bitarray.BitArrayVersion;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.bitarray.EmptyBitArray;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockPaletteTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.TunnelBlockState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@RequiredArgsConstructor
public class DecodedPaletteStorage {
    private final BitArray bitArray;
    private final Map<Integer, Integer> palette;

    public int get(int x, int y, int z) {
        return palette.getOrDefault(bitArray.get(index(x, y, z)), 0);
    }

    public void set(int x, int y, int z, int value) {
        if(!palette.containsValue(value)) {
            throw new IllegalArgumentException("Value does not exist in palette");
        }
        int id = 0;
        for(Map.Entry<Integer, Integer> entry : palette.entrySet()) {
            if(entry.getValue().equals(value)) {
                id = entry.getKey();
            }
        }
        bitArray.set(index(x, y, z), id);
    }

    private static int index(int x, int y, int z) {
        return (x << 8) + (z << 4) + y;
    }

    public static DecodedPaletteStorage fromPacket(ByteBuf byteBuf, Function<ByteBuf, Integer> encoding) {
        int paletteHeader = byteBuf.readUnsignedByte();
        boolean isRuntime = (paletteHeader & 1) == 1;
        int paletteVersion = (paletteHeader | 1) >> 1;

        if(paletteHeader >> 1 == 0x7f) {
            return null;
        }

        BitArrayVersion bitArrayVersion = BitArrayVersion.get(paletteVersion, true);

        int maxBlocksInSection = 4096;
        BitArray bitArray = bitArrayVersion.createPalette(maxBlocksInSection);
        int wordsSize = bitArrayVersion.getWordsForSize(maxBlocksInSection);
        if(!byteBuf.isReadable(wordsSize * 4)) {
            return new DecodedPaletteStorage(BitArrayVersion.V0.createPalette(maxBlocksInSection), new HashMap<>());
        }

        for (int wordIterationIndex = 0; wordIterationIndex < wordsSize; wordIterationIndex++) {
            int word = byteBuf.readIntLE();
            bitArray.getWords()[wordIterationIndex] = word;
        }

        int paletteSize = 1;
        if(!(bitArray instanceof EmptyBitArray)) {
            paletteSize = VarInts.readInt(byteBuf);
        }

        Map<Integer, Integer> palette = new HashMap<>();
        for (int i = 0; i < paletteSize; i++) {
            if (isRuntime) {
                palette.put(i, VarInts.readInt(byteBuf));
            } else {
                palette.put(i, encoding.apply(byteBuf));
            }
        }

        return new DecodedPaletteStorage(bitArray, palette);
    }

    public static final Function<ByteBuf, Integer> BIOME_PALETTE = ByteBuf::readIntLE;
    public static final Function<ByteBuf, Integer> BLOCK_PALETTE = byteBuf -> {
        try(NBTInputStream nbtStream = new NBTInputStream(new NetworkDataInputStream(new ByteBufInputStream(byteBuf)))) {
            NbtMap nbt = (NbtMap) nbtStream.readTag();
            NbtMapBuilder map = nbt.toBuilder();

            map.replace("name", "minecraft:" + map.get("name").toString());
            return BlockPaletteTranslator.getBedrockBlockId(TunnelBlockState.getStateFromNBTMap(map.build()));
        } catch (IOException e) {
            log.catching(e);
        }

        return null;
    };
}
