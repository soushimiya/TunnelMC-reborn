package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.nukkitx.nbt.NbtMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@EqualsAndHashCode
public class TunnelBlockState {
	private final String namespace;
	private final String identifier;
	//this holds the block states properties, for example, stone_type, granite
	private final Map<String, String> properties;

	public TunnelBlockState(String identifier) {
		this(identifier.split(":")[0], identifier.split(":")[1]);
	}

	public TunnelBlockState(String namespace, String identifier) {
		this(namespace, identifier, new HashMap<>());
	}

	public TunnelBlockState(String namespace, String identifier, Map<String, String> properties) {
		this.namespace = namespace;
		this.identifier = identifier;
		this.properties = Collections.unmodifiableMap(properties);
	}

	public static TunnelBlockState getStateFromString(String string) {
		String namespace = "minecraft";
		int firstColonIndex = string.indexOf(":");
		if (firstColonIndex != -1) {
			namespace = string.substring(0, firstColonIndex);
		}else{
			firstColonIndex = 0;
		}

		String identifier;
		int firstLeftBracketIndex = string.indexOf("[");
		if (firstLeftBracketIndex != -1) {//if its found
			identifier = string.substring(firstColonIndex, firstLeftBracketIndex);
		} else {
			identifier = string.substring(firstColonIndex);
		}

		Map<String, String> properties = new HashMap<>();
		if (firstLeftBracketIndex != -1) {
			String blockProperties = string.substring(firstLeftBracketIndex + 1, string.length() - 1);
			String[] blockProperyKeysAndValues = blockProperties.split(",");

			for (String keyAndValue : blockProperyKeysAndValues) {
				String[] keyAndValueArray = keyAndValue.split("=");

				properties.put(keyAndValueArray[0], keyAndValueArray[1]);
			}
		}

		return new TunnelBlockState(namespace, identifier, properties);
	}

	public static TunnelBlockState getStateFromNBTMap(NbtMap nbtMap) {
		String blockName = nbtMap.getString("name");
		NbtMap blockStates = nbtMap.getCompound("states");

		String namespace = "minecraft";
		int firstColonIndex = blockName.indexOf(":");
		if (firstColonIndex != -1) {
			namespace = blockName.substring(0, firstColonIndex);
		}else{
			firstColonIndex = 0;
		}

		String identifier = blockName.substring(firstColonIndex);

		Map<String, String> properties = new HashMap<>();
		for (Map.Entry<String, Object> blockState : blockStates.entrySet()) {
			properties.put(blockState.getKey(), blockState.getValue().toString());
		}

		return new TunnelBlockState(namespace, identifier, properties);
	}

	public boolean isVanilla() {
		return this.getNamespace().equals("minecraft");
	}

	public Block getVanillaBlock() {
		Block block = Registry.BLOCK.get(new Identifier(this.getNamespace(), this.getIdentifier()));
		if (block == Blocks.AIR && !this.getIdentifier().equals("air")) {
			return null;
		}

		return block;
	}

	public BlockState getBlockState() {
		Block block = this.getVanillaBlock();
		BlockState blockState = block.getDefaultState();

		for(Map.Entry<String, String> stateEntry : this.getProperties().entrySet()) {
			Property<?> property = block.getStateManager().getProperty(stateEntry.getKey());
			if (property == null) {
				throw new NullPointerException("Could not find property " + stateEntry.getKey());
			}

			blockState = parsePropertyValue(blockState, property, stateEntry.getValue());
			if (blockState == null) {
				throw new NullPointerException("Could not find state " + stateEntry.getKey() + " or set the value " + stateEntry.getValue() + ": " + stateEntry);
			}
		}

		return blockState;
	}

	private static <T extends Comparable<T>> BlockState parsePropertyValue(BlockState before, Property<T> property, String value) {
		Optional<T> optional = property.parse(value);
		return optional.map(t -> before.with(property, t)).orElse(null);
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean withProperties) {
		if (this.identifier == null || this.namespace == null) {
			return null;
		}

		StringBuilder string = new StringBuilder(this.namespace);
		string.append(":").append(this.identifier);

		if(this.properties.size() >= 1 && withProperties) {
			string.append("[");
			for (Map.Entry<String, String> entry : this.properties.entrySet()) {
				string.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
			}
			string = new StringBuilder(string.substring(0, string.length() - 1)); // remove the last comma
			string.append("]");
		}

		return string.toString();
	}
}
