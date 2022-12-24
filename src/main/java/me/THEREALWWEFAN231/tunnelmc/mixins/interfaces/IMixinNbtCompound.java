package me.THEREALWWEFAN231.tunnelmc.mixins.interfaces;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(NbtCompound.class)
public interface IMixinNbtCompound {

    @Accessor @Final
    Map<String, NbtElement> getEntries();
}
