package me.THEREALWWEFAN231.tunnelmc.translator.dimension;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Arrays;
import java.util.Optional;

public enum Dimension {
    OVERWORLD(DimensionTypes.OVERWORLD, World.OVERWORLD, 0),
    THE_NETHER(DimensionTypes.THE_NETHER, World.NETHER, 1),
    THE_END(DimensionTypes.THE_NETHER, World.END, 2);

    private final RegistryKey<DimensionType> dimensionRegistryKey;
    private final RegistryKey<World> worldRegistryKey;
    private final int bedrockDimensionId;

    Dimension(RegistryKey<DimensionType> dimensionRegistryKey, RegistryKey<World> worldRegistryKey, int bedrockDimensionId) {
        this.dimensionRegistryKey = dimensionRegistryKey;
        this.worldRegistryKey = worldRegistryKey;
        this.bedrockDimensionId = bedrockDimensionId;
    }

    public static Optional<Dimension> getDimensionFromId(int id) {
        return Arrays.stream(Dimension.values()).filter(dimension -> dimension.bedrockDimensionId == id).findFirst();
    }

    public RegistryKey<DimensionType> getDimensionRegistryKey() {
        return dimensionRegistryKey;
    }

    public RegistryKey<World> getWorldRegistryKey() {
        return worldRegistryKey;
    }

    public int getBedrockDimensionId() {
        return bedrockDimensionId;
    }
}
