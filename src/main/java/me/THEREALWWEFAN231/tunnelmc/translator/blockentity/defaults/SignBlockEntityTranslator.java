package me.THEREALWWEFAN231.tunnelmc.translator.blockentity.defaults;

import com.nukkitx.nbt.NbtMap;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityTranslator;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;

public class SignBlockEntityTranslator extends BlockEntityTranslator {
    @Override
    public NbtCompound translateTag(NbtMap bedrockNbt, NbtCompound newTag) {
        String text = bedrockNbt.getString("Text");
        String[] javaText = {"", "", "", ""};

        //TODO: Improve this - I want to figure out if we can use Minecraft internals before using Geyser's sign wrapping implementation
        int textCount = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                javaText[textCount++] = builder.toString();
                if (textCount > 3) {
                    break;
                }
                builder = new StringBuilder();
                continue;
            }
            builder.append(c);
        }

        for(int i = 0; i < javaText.length; i++) {
            TextComponent component = LegacyComponentSerializer.legacySection().deserialize(javaText[i]);
            newTag.putString("Text" + (i + 1), GsonComponentSerializer.gson().serialize(component));
        }
        return newTag;
    }

    @Override
    public BlockEntityType<?> getJavaId() {
        return BlockEntityType.SIGN;
    }
}
