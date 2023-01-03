package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

public class DynamicContainer extends GenericContainer {
    public DynamicContainer(int size) {
        super(size);
    }

    @Override
    protected int convertJavaSlotIdToBedrockSlotId(int javaSlotId) {
        return javaSlotId;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
