package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityDataCache {
	private final Map<Vector3i, NbtMap> cachedBlockPositionsData = new HashMap<>();

	public Map<Vector3i, NbtMap> getCachedBlockPositionsData() {
		return this.cachedBlockPositionsData;
	}
	
	public NbtMap getDataFromBlockPosition(Vector3i blockPosition) {
		return this.cachedBlockPositionsData.get(blockPosition);
	}
}
