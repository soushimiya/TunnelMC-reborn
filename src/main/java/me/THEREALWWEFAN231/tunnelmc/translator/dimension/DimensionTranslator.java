package me.THEREALWWEFAN231.tunnelmc.translator.dimension;

import me.THEREALWWEFAN231.tunnelmc.utils.DimensionUtils;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class DimensionTranslator {

	public static DimensionType bedrockToJava(int bedrockDimensionId) {
		//im not even fully sure about the bedrock dimension ids
		if (bedrockDimensionId == 0) {
			return DimensionUtils.getOverworld();
		} else if (bedrockDimensionId == 1) {
			return DimensionUtils.getTheNether();
		} else if (bedrockDimensionId == 2) {
			return DimensionUtils.getTheEnd();
		}
		return DimensionUtils.getOverworld();
	}

	public static RegistryKey<World> bedrockToJavaRegistryKey(int bedrockDimensionId) {
		if (bedrockDimensionId == 0) {
			return World.OVERWORLD;
		} else if (bedrockDimensionId == 1) {
			return World.NETHER;
		} else if (bedrockDimensionId == 2) {
			return World.END;
		}

		return World.OVERWORLD;
	}

}
