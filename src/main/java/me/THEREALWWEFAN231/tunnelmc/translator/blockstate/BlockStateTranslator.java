package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Log4j2
public class BlockStateTranslator {
	//TODO: create an override file, which allows us to override the blocks.json information and or create new information, once we do that fix all the printlns' from BlockPaletteTranslator
	//TODO: i also dont think water logged blocks work, i cant test right now as i cant connect to a dedicated bedrock server

	public static final Map<TunnelBlockState, TunnelBlockState> JAVA_TO_BEDROCK = new HashMap<>();
	public static final Map<TunnelBlockState, TunnelBlockState> BEDROCK_TO_JAVA = new HashMap<>();

	public static void load() {
		ObjectNode j2b = (ObjectNode) FileUtils.getJsonFromResource("prismarinejs/blocksJ2B.json");
		ObjectNode b2j = (ObjectNode) FileUtils.getJsonFromResource("prismarinejs/blocksB2J.json");
		if(j2b == null || b2j == null) {
			throw new NullPointerException("Cannot find/use blockstate translator files");
		}

		for (Iterator<Map.Entry<String, JsonNode>> it = j2b.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(entry.getKey());
			TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromString(entry.getValue().asText());

			JAVA_TO_BEDROCK.put(javaBlockState, bedrockBlockState);
		}
		for (Iterator<Map.Entry<String, JsonNode>> it = b2j.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			TunnelBlockState bedrockBlockState = TunnelBlockState.getStateFromString(entry.getKey());
			TunnelBlockState javaBlockState = TunnelBlockState.getStateFromString(entry.getValue().asText());

			BEDROCK_TO_JAVA.put(bedrockBlockState, javaBlockState);
		}

		InputStream stream = FileUtils.class.getClassLoader().getResourceAsStream("tunnel/block_palette.nbt");
		if (stream == null) {
			throw new RuntimeException("Could not find the block palette file!");
		}

		try (NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(new GZIPInputStream(stream)))) {
			NbtMap blockPalette = (NbtMap) nbtInputStream.readTag();
			NbtList<NbtMap> blocksTag = (NbtList<NbtMap>) blockPalette.getList("blocks", NbtType.COMPOUND);
			BlockPaletteTranslator.loadMap(blocksTag);
		} catch (Exception e) {
			throw new AssertionError("Unable to get blocks from runtime block states", e);
		}
	}
}
