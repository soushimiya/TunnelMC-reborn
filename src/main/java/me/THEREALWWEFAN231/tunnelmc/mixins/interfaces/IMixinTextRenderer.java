package me.THEREALWWEFAN231.tunnelmc.mixins.interfaces;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(TextRenderer.class)
public interface IMixinTextRenderer {
    @Accessor @Final
    Function<Identifier, FontStorage> getFontStorageAccessor();
}
