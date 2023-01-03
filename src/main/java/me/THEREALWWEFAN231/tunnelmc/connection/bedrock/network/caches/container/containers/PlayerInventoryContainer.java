package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

public class PlayerInventoryContainer extends GenericContainer {
	private static final int SIZE = 36;

	public PlayerInventoryContainer() {
		super(PlayerInventoryContainer.SIZE);
	}
	
	@Override
	protected int convertJavaSlotIdToBedrockSlotId(int javaSlotId) {
		if(javaSlotId >= 36) {//if it's a java hotbar slot 36->44
			return javaSlotId - 36;//convert to bedrock slot, 0-8
		}
		
		//this check *isn't* needed *if* we are in the correct container, which we should be, for now I'm keeping this if statement, and return 0 for debugging purposes
		if(javaSlotId >= 9) {
			return javaSlotId;//java main inventory, the 27 slots have the same id on bedrock
		}
		
		return super.convertJavaSlotIdToBedrockSlotId(javaSlotId);
	}

	@Override
	public boolean isStatic() {
		return true;
	}
}
