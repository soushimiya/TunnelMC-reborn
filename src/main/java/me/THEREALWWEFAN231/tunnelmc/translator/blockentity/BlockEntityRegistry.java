package me.THEREALWWEFAN231.tunnelmc.translator.blockentity;

import com.nukkitx.nbt.NbtMap;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults.ChestBlockEntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults.GenericBlockEntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults.SignBlockEntityTranslator;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityRegistry {
    private static final Map<String, BlockEntityTranslator> BLOCK_ENTITY_TRANSLATORS = new HashMap<>();

    public static BlockEntityTranslator getBlockEntityTranslator(NbtMap bedrockNbt) {
        return BLOCK_ENTITY_TRANSLATORS.get(bedrockNbt.getString("id"));
    }

    public static void load() {
        BLOCK_ENTITY_TRANSLATORS.put("Sign", new SignBlockEntityTranslator());
        BLOCK_ENTITY_TRANSLATORS.put("Chest", new ChestBlockEntityTranslator());
        BLOCK_ENTITY_TRANSLATORS.put("EnderChest", new GenericBlockEntityTranslator(BlockEntityType.ENDER_CHEST));
    }
}
