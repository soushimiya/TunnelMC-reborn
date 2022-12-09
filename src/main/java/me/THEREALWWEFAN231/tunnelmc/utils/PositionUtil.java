package me.THEREALWWEFAN231.tunnelmc.utils;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;

@UtilityClass
public class PositionUtil {

    public BlockPos toBlockPos(Vector3i vector) {
        return new BlockPos(vector.getX(), vector.getY(), vector.getZ());
    }

    public BlockPos toBlockPos(Vector3f vector) {
        return new BlockPos(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3i toBedrockVector3i(BlockPos blockPos) {
        return Vector3i.from(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vector3f toBedrockVector3f(BlockPos blockPos) {
        return Vector3f.from(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
