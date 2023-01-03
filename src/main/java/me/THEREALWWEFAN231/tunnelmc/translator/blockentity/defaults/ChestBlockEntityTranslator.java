package me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults;

import com.nukkitx.math.vector.Vector2i;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtils;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class ChestBlockEntityTranslator extends BlockEntityTranslator {
    @Override
    public NbtCompound translateTag(NbtMap bedrockNbt, NbtCompound newTag) {
        this.putIfExists(bedrockNbt, newTag, "pairx", (Function<Integer, NbtElement>) NbtInt::of);
        this.putIfExists(bedrockNbt, newTag, "pairz", (Function<Integer, NbtElement>) NbtInt::of);

        int y = bedrockNbt.getInt("y");
        Vector2i xz = Vector2i.from(bedrockNbt.getInt("x"), bedrockNbt.getInt("z"));
        BlockPos blockPos = PositionUtils.toBlockPos(Vector3i.from(xz.getX(), y, xz.getY()));
        if(bedrockNbt.containsKey("pairx", NbtType.INT) && bedrockNbt.containsKey("pairz", NbtType.INT)) {
            Vector2i pairXZ = Vector2i.from(bedrockNbt.getInt("pairx"), bedrockNbt.getInt("pairz"));

            float distance = xz.distance(pairXZ);
            if(distance != 1) {
                throw new IllegalStateException("Cannot this chest link from more or less blocks than 1");
            }

//            TODO
//            Vector2i diff = xz.sub(pairXZ);
//            BlockPos diffBlockPos = PositionUtils.toBlockPos(Vector3i.from(diff.getX(), 0, diff.getY()));
//            Direction direction = Direction.fromVector(diffBlockPos);
//            BlockState state = TunnelMC.mc.world.getBlockState(blockPos);
//            BlockState neighborState = TunnelMC.mc.world.getBlockState(PositionUtils.toBlockPos(Vector3i.from(pairXZ.getX(), y, pairXZ.getY())));
//
//            BlockState newState = null;
//            if (neighborState.isOf(state.getBlock()) && direction.getAxis().isHorizontal()) {
//                ChestType chestType = neighborState.get(ChestBlock.CHEST_TYPE);
//                if (state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && state.get(ChestBlock.FACING) == neighborState.get(ChestBlock.FACING) && ChestBlock.getFacing(neighborState) == direction.getOpposite()) {
//                    newState = state.with(ChestBlock.CHEST_TYPE, chestType.getOpposite());
//                }
//            } else if (ChestBlock.getFacing(state) == direction) {
//                newState = state.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE);
//            }
//
//            if(newState != null) {
//                BlockUpdateS2CPacket blockUpdateS2CPacket = new BlockUpdateS2CPacket(blockPos, newState);
//                blockUpdateS2CPacket.apply(TunnelMC.mc.getNetworkHandler());
//            }
        }

        return newTag;
    }

    private void putIfExists(NbtMap bedrockNbt, NbtCompound newTag, String key, Function<?, NbtElement> supplier) {
        if(bedrockNbt.containsKey(key)) {
            newTag.put(key, ((Function<Object, NbtElement>) supplier).apply(bedrockNbt.get(key)));
        }
    }

    @Override
    public BlockEntityType<?> getJavaId() {
        return BlockEntityType.CHEST;
    }
}
