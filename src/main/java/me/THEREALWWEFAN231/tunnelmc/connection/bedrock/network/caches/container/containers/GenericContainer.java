package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;

public class GenericContainer extends BedrockContainer {
    public GenericContainer(int size, int id) {
        super(size, id);
    }

    @Override
    public int convertJavaSlotIdToBedrockSlotId(int javaSlotId) {
        return 0;
    }
}
