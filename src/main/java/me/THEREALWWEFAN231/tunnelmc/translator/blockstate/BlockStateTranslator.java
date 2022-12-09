package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockStateTranslator {
	//TODO: create an override file, which allows us to override the blocks.json information and or create new information, once we do that fix all the printlns' from BlockPaletteTranslator
	//TODO: i also dont think water logged blocks work, i cant test right now as i cant connect to a dedicated bedrock server

	public static final HashMap<String, BlockState> BEDROCK_BLOCK_STATE_STRING_TO_JAVA_BLOCK_STATE = new HashMap<String, BlockState>();

	public static void load() {
		ObjectNode jsonObject = (ObjectNode) FileUtils.getJsonFromResource("geyser/blocks.json");
		if(jsonObject == null) {
			return;
		}

		for (Iterator<Map.Entry<String, JsonNode>> it = jsonObject.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			String javaBlockState = entry.getKey(); // could be for example, wheat[age=0]
			JsonNode blockEntry = entry.getValue();

			BedrockBlockState bedrockBlockState = new BedrockBlockState();
			bedrockBlockState.identifier = blockEntry.get("bedrock_identifier").asText();

			if (blockEntry.has("bedrock_states")) {
				ObjectNode states = (ObjectNode) blockEntry.get("bedrock_states");
				for (Iterator<Map.Entry<String, JsonNode>> iter = states.fields(); iter.hasNext(); ) {
					Map.Entry<String, JsonNode> stateEntry = iter.next();
					if (!(stateEntry.getValue() instanceof ValueNode)) {
						continue;
					}

					JsonNode node = stateEntry.getValue();
					String value;

					if (node.isBoolean()) {
						value = String.valueOf(node.asBoolean());
					} else if (node.isNumber()/* && jsonPrimitive.getAsNumber() instanceof Integer*/) {//gson uses "LazilyParsedNumber" ree, this causes me some pain, hopefully we can find a better solution some time, that doesn't use jsonPrimitive.toString, although, its not needed right now, as there are no double properties
						value = String.valueOf(node.numberValue());
					} else if (node.isTextual()) {
						value = node.asText();
					} else {
						System.out.println("Unknown block state value, key=" + stateEntry.getKey() + " value=" + node + ":" + node.getClass());
						continue;
					}

					bedrockBlockState.properties.put(stateEntry.getKey(), value);
				}
			}

			if (entry.getKey().equals("minecraft:water[level=1]")) {
				System.out.println(bedrockBlockState.toString());
			}

			BlockState blockState = BlockStateTranslator.parseBlockState(javaBlockState);
			if (blockState == null) { // we print in the parseBlockState method
				continue;
			}

			BEDROCK_BLOCK_STATE_STRING_TO_JAVA_BLOCK_STATE.put(bedrockBlockState.toString(), blockState);

		}

		InputStream stream = FileUtils.class.getClassLoader().getResourceAsStream("tunnel/block_palette.nbt");
		if (stream == null) {
			throw new RuntimeException("Could not find the block palette file!");
		}

		NbtList<NbtMap> blocksTag;
		try (NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(new GZIPInputStream(stream)))) {
			NbtMap blockPalette = (NbtMap) nbtInputStream.readTag();
			blocksTag = (NbtList<NbtMap>) blockPalette.getList("blocks", NbtType.COMPOUND);
		} catch (Exception e) {
			throw new AssertionError("Unable to get blocks from runtime block states", e);
		}
		BlockPaletteTranslator.loadMap(blocksTag);

	}

	private static BlockState parseBlockState(String blockStateInformation) {//parses for example wheat[age=0]
		String javaBlockIdentifier;

		int firstLeftBracketIndex = blockStateInformation.indexOf("[");
		if (firstLeftBracketIndex != -1) {//if its found
			javaBlockIdentifier = blockStateInformation.substring(0, firstLeftBracketIndex);
		} else {
			javaBlockIdentifier = blockStateInformation;
		}

		Block block = Registry.BLOCK.get(new Identifier(javaBlockIdentifier));
		//do not use block instanceof AirBlock, as there is void_air and cave_air, i guess, never knew they existed
		if (block == Blocks.AIR && !javaBlockIdentifier.equals("minecraft:air")) {//Registry.BLOCK.get returns air if its not found, so if this is true, the block is not found, and this generally isn't good
			System.out.println(javaBlockIdentifier + " block was not found, this generally isn't good.");
			return null;
		}

		BlockState theBlockState = block.getDefaultState();

		if (firstLeftBracketIndex != -1) {
			String blockProperties = blockStateInformation.substring(firstLeftBracketIndex + 1, blockStateInformation.length() - 1);

			String[] blockProperyKeysAndValues = blockProperties.split(",");

			for (String keyAndValue : blockProperyKeysAndValues) {
				String[] keyAndValueArray = keyAndValue.split("=");
				String key = keyAndValueArray[0];
				String value = keyAndValueArray[1];

				Property<?> property = block.getStateManager().getProperty(key);
				if (property == null) {
					System.out.println("Could not find the property " + key);
					return null;
				}

				theBlockState = parsePropertyValue(theBlockState, property, value);
				if (theBlockState == null) {
					System.out.println("Could not find the state " + key + " or set the value " + value + " " + blockStateInformation);
					return null;
				}
			}

		}

		return theBlockState;

	}

	private static <T extends Comparable<T>> BlockState parsePropertyValue(BlockState before, Property<T> property, String value) {
		Optional<T> optional = property.parse(value);
		return optional.map(t -> before.with(property, t)).orElse(null);
	}
}
