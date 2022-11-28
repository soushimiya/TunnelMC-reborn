package me.THEREALWWEFAN231.tunnelmc.utils;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

public final class DimensionUtils {
    private static final DimensionType OVERWORLD = BuiltinRegistries.DIMENSION_TYPE.get(DimensionTypes.OVERWORLD);
    private static final DimensionType THE_NETHER = BuiltinRegistries.DIMENSION_TYPE.get(DimensionTypes.THE_NETHER);
    private static final DimensionType THE_END = BuiltinRegistries.DIMENSION_TYPE.get(DimensionTypes.THE_END);

    public static DimensionType getOverworld() {
        return OVERWORLD;
    }

    public static DimensionType getTheNether() {
        return THE_NETHER;
    }

    public static DimensionType getTheEnd() {
        return THE_END;
    }
}
