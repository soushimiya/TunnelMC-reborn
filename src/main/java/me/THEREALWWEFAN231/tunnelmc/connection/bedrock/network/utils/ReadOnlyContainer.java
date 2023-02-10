package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils;

import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ReadOnlyContainer extends BedrockContainer{
    @Getter
    private final BedrockContainer wrapped;

    private ReadOnlyContainer(BedrockContainer wrapped) {
        super(wrapped.getSize());
        this.wrapped = wrapped;
    }

    public static BedrockContainer wrap(BedrockContainer container) {
        return new ReadOnlyContainer(container);
    }

    public void setItemBedrock(int slot, ItemData itemData) {
        throw new UnsupportedOperationException();
    }

    public ItemData getItemFromSlot(int slot) {
        return this.wrapped.getItemFromSlot(slot).toBuilder().build();
    }

    public int getSize() {
        return this.wrapped.getSize();
    }

    public List<ItemData> getItems() {
        return Collections.unmodifiableList(this.wrapped.getItems());
    }

    @Override
    public int getJavaSlotId(int bedrockSlotId) {
        return wrapped.getJavaSlotId(bedrockSlotId);
    }

    @Override
    public int getBedrockSlotId(int javaSlotId) {
        return wrapped.getBedrockSlotId(javaSlotId);
    }

    @Override
    public boolean isStatic() {
        return this.wrapped.isStatic();
    }

    @Override
    public void updateInventory() {
        this.wrapped.updateInventory();
    }
}
