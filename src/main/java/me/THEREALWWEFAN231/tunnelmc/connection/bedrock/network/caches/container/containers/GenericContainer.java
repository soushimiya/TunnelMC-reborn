package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.utils.BedrockContainer;

public abstract class GenericContainer extends BedrockContainer {
    public GenericContainer(int size) {
        super(size);
    }

    @Override
    public int getJavaSlotId(int bedrockSlotId) {
        return bedrockSlotId;
    }

    @Override
    public int getBedrockSlotId(int javaSlotId) {
        return javaSlotId;
    }
}
