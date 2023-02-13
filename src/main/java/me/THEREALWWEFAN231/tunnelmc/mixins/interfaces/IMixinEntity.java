package me.THEREALWWEFAN231.tunnelmc.mixins.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Entity.class)
public interface IMixinEntity {
    @Accessor("dataTracker")
    DataTracker getDataTracker();
    @Accessor("uuid")
    UUID getUuid();
}
