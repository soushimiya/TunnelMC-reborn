package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

@Getter
@RequiredArgsConstructor
public final class EntityMetadataPair<T> {
    private final Entity entity;
    private final T value;
}
