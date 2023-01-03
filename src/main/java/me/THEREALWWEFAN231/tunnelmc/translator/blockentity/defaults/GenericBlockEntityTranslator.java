package me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults;

import com.nukkitx.nbt.NbtMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityTranslator;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;

@Getter
@RequiredArgsConstructor
public class GenericBlockEntityTranslator extends BlockEntityTranslator {
    private final BlockEntityType<?> javaId;

    @Override
    public NbtCompound translateTag(NbtMap bedrockNbt, NbtCompound newTag) {
        return newTag;
    }
}
