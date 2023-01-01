package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.bitarray;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyBitArray implements BitArray {
	final static BitArray INSTANCE = new EmptyBitArray();

	@Override
	public void set(int index, int value) {
		// NOOP
	}

	@Override
	public int get(int index) {
		return 0;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public int[] getWords() {
		return new int[0];
	}

	@Override
	public BitArrayVersion getVersion() {
		return BitArrayVersion.V0;
	}

	@Override
	public BitArray copy() {
		return INSTANCE;
	}
}
