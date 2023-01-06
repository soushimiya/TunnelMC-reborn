package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils;

import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

import java.util.*;

public class ActionBuilder {
    private final InventorySource source;
    private final BedrockContainer container;
    private final Map<Integer, ItemData> actions;

    ActionBuilder(InventorySource source, BedrockContainer container, Map<Integer, ItemData> actions) {
        this.source = source;
        this.container = container;
        this.actions = actions;
    }

    public List<InventoryActionData> execute() {
        List<InventoryActionData> actions = new ArrayList<>();

        BedrockContainer container = this.container;
        if(container instanceof ReadOnlyContainer) {
            container = ((ReadOnlyContainer) container).getWrapped();
        }

        for(Map.Entry<Integer, ItemData> entry : this.actions.entrySet()) {
            actions.add(new InventoryActionData(this.source, entry.getKey(), container.getItemFromSlot(entry.getKey()), entry.getValue()));
            container.setItemBedrock(entry.getKey(), entry.getValue());
        }

        return actions;
    }

    public void revert() {
        this.container.updateInventory();
    }

    public static ActionBuilderBuilder builder() {
        return new ActionBuilderBuilder();
    }

    public static class ActionBuilderBuilder {
        private InventorySource source;
        private BedrockContainer container;
        private final Map<Integer, ItemData> actions = new HashMap<>();

        ActionBuilderBuilder() {
        }

        public ActionBuilderBuilder container(InventorySource source, BedrockContainer container) {
            this.source = source;
            this.container = container;
            return this;
        }

        public ItemData slot(int slot) {
            if(this.container == null) {
                return null;
            }

            return this.container.getItemFromSlot(slot);
        }

        public ActionBuilderBuilder action(int slot, ItemData action) {
            this.actions.put(slot, action);
            return this;
        }

        public ActionBuilderBuilder clearActions() {
            this.actions.clear();
            return this;
        }

        public ActionBuilder build() {
            if(this.container == null) {
                throw new NullPointerException("container");
            }

            Map<Integer, ItemData> actions = Collections.unmodifiableMap(this.actions);

            return new ActionBuilder(this.source, this.container, actions);
        }
    }
}
