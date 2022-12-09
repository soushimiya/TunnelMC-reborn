package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.LevelChunkDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.ChunkTickScheduler;

import java.util.Objects;

/**
 * For more information, check out the following gist:
 * <a href="https://gist.github.com/dktapps/8a4f23d2bf32ea7091ef14e4aac46170">Block Changes in Beta 1.2.13</a>
 */
@PacketIdentifier(LevelChunkTranslator.class)
public class LevelChunkTranslator extends PacketTranslator<LevelChunkPacket> {
	private static final Registry<Biome> BIOMES_REGISTRY = BuiltinRegistries.BIOME;

	@Override
	public void translate(LevelChunkPacket packet, Client client) {
		int chunkX = packet.getChunkX();
		int chunkZ = packet.getChunkZ();

		ChunkSection[] chunkSections = new ChunkSection[24];

		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(packet.getData());

		for (int sectionIndex = 0; sectionIndex < packet.getSubChunksLength(); sectionIndex++) {
			ChunkSection chunkSection = new ChunkSection(sectionIndex, BIOMES_REGISTRY);
			int chunkVersion = byteBuf.readByte();
			if (chunkVersion != 1 && chunkVersion != 8) {
//				System.out.println("Decoding a version zero chunk...");
				LevelChunkDecoder.networkDecodeVersionZero(byteBuf, chunkSection);
			} else if (chunkVersion == 1) {
//				System.out.println("Decoding a version one chunk...");
				LevelChunkDecoder.networkDecodeVersionOne(byteBuf, chunkSection);
			} else {
//				System.out.println("Decoding a version eight chunk...");
				LevelChunkDecoder.networkDecodeVersionEight(byteBuf, chunkSection, byteBuf.readByte());
			}
			chunkSections[sectionIndex] = chunkSection;
		}

//		byte[] bedrockBiomes = new byte[256];
//		byteBuf.readBytes(bedrockBiomes);
		byteBuf.readBytes(byteBuf.readableBytes());

		// TODO: BIOMES
		// TODO: Block entities

//		int[] javaBiomes = new int[1024];
//		int javaBiomesCount = 0;
//		for (ChunkSection chunkSection : chunkSections) {
//			PalettedContainer<RegistryEntry<Biome>> container = (PalettedContainer<RegistryEntry<Biome>>) chunkSection.getBiomeContainer();
//			byte desiredBiome = bedrockBiome;
//
//			if (BIOMES_REGISTRY.get(desiredBiome) == null) {
//				desiredBiome = 1;
//			}
//
//			for ()
//			container.set();
//			javaBiomes[javaBiomesCount++] = desiredBiome;
//			javaBiomes[javaBiomesCount++] = desiredBiome;
//			javaBiomes[javaBiomesCount++] = desiredBiome;
//			javaBiomes[javaBiomesCount++] = desiredBiome;
//		}

		Runnable runnable = () -> {
			WorldChunk worldChunk = new WorldChunk(Objects.requireNonNull(TunnelMC.mc.world), new ChunkPos(chunkX, chunkZ), UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler<>(), new ChunkTickScheduler<>(), 0, chunkSections, null, null);

			ChunkDataS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDataS2CPacket(worldChunk, TunnelMC.mc.world.getLightingProvider(), null, null, true);
			client.javaConnection.processServerToClientPacket(chunkDeltaUpdateS2CPacket);
		};

		if (TunnelMC.mc.world != null) {
			runnable.run();
		} else {
			MinecraftClient.getInstance().execute(runnable);
		}
	}
}
