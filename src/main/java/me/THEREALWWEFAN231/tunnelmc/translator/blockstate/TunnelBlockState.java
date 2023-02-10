package me.THEREALWWEFAN231.tunnelmc.translator.blockstate;

import com.nukkitx.nbt.NbtMap;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

@Getter
@Log4j2
public class TunnelBlockState {
	private final String namespace;
	private final String identifier;
	//this holds the block states properties, for example, stone_type, granite
	private final Map<String, String> properties;

	public TunnelBlockState(String namespaceAndIdentifier) {
		this(namespaceAndIdentifier, new HashMap<>());
	}

	public TunnelBlockState(String namespaceAndIdentifier, Map<String, String> properties) {
		this(namespaceAndIdentifier.split(":")[0], namespaceAndIdentifier.split(":")[1], properties);
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
			namespace = string.substring(0, firstColonIndex++);
		}else{
			firstColonIndex = 0;
		}

		String identifier;
		int firstLeftBracketIndex = string.indexOf("[");
		if (firstLeftBracketIndex != -1) {//if its found
			identifier = string.substring(firstColonIndex, firstLeftBracketIndex++);
		} else {
			identifier = string.substring(firstColonIndex);
		}

		Map<String, String> properties = new HashMap<>();
		if (firstLeftBracketIndex != -1) {
			String blockProperties = string.substring(firstLeftBracketIndex, string.length() - 1);
			if(!blockProperties.isEmpty()) {
				String[] blockPropertyKeysAndValues = blockProperties.split(",");

				for (String keyAndValue : blockPropertyKeysAndValues) {
					String[] keyAndValueArray = keyAndValue.split("=");

					properties.put(keyAndValueArray[0], keyAndValueArray[1]);
				}
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
			namespace = blockName.substring(0, firstColonIndex++);
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
		if(block == null) {
			return null;
		}
		BlockState blockState = block.getDefaultState();

		for(Map.Entry<String, String> stateEntry : this.getProperties().entrySet()) {
			Property<?> property = block.getStateManager().getProperty(stateEntry.getKey());
			if (property == null) {
				log.debug("{}: Could not find property {}", this.toString(), stateEntry.getKey());
				continue;
			}

			BlockState before = blockState;
			blockState = parsePropertyValue(blockState, property, stateEntry.getValue());
			if (blockState == null) {
				log.debug("{}: Could not find state {} or set the value {}: {}", this.toString(), stateEntry.getKey(), stateEntry.getValue(), stateEntry);
				blockState = before;
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

	public boolean equals(final Object o) {
		return equals(o, true);
	}

	public boolean equals(final Object o, boolean includeProperties) {
		if (o == this) return true;
		if (!(o instanceof final TunnelBlockState other)) return false;
		if (!other.canEqual(this)) return false;
		final Object this$namespace = this.getNamespace();
		final Object other$namespace = other.getNamespace();
		if (!Objects.equals(this$namespace, other$namespace)) return false;
		final Object this$identifier = this.getIdentifier();
		final Object other$identifier = other.getIdentifier();
		if (!Objects.equals(this$identifier, other$identifier)) return false;

		if(includeProperties) {
			if (this.getProperties().size() != other.getProperties().size()) return false;

			for (Map.Entry<String, String> this$propertyEntry : this.getProperties().entrySet()) {
				String key = this$propertyEntry.getKey();

				String this$propertyEntryValue = this$propertyEntry.getValue();
				if(this$propertyEntryValue.equals("true") || this$propertyEntryValue.equals("false")) {
					this$propertyEntryValue = Boolean.parseBoolean(this$propertyEntryValue) ? "1" : "0";
				}
				String other$propertyEntryValue = other.getProperties().get(key);
				if(other$propertyEntryValue.equals("true") || other$propertyEntryValue.equals("false")) {
					other$propertyEntryValue = Boolean.parseBoolean(other$propertyEntryValue) ? "1" : "0";
				}

				if (!Objects.deepEquals(this$propertyEntryValue, other$propertyEntryValue)) {
					return false;
				}
			}
		}

		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof TunnelBlockState;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $namespace = this.getNamespace();
		result = result * PRIME + ($namespace == null ? 43 : $namespace.hashCode());
		final Object $identifier = this.getIdentifier();
		result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
		final Object $properties = this.getProperties();
		result = result * PRIME + ($properties == null ? 43 : $properties.hashCode());
		return result;
	}
}
