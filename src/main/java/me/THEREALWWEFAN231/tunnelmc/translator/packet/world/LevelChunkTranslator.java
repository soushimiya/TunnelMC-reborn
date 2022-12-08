package me.THEREALWWEFAN231.tunnelmc.translator.packet.world;

import com.nukkitx.api.event.Listener;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.events.EventPlayerTick;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.tick.ChunkTickScheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * For more information, check out the following gist:
 * <a href="https://gist.github.com/dktapps/8a4f23d2bf32ea7091ef14e4aac46170">Block Changes in Beta 1.2.13</a>
 */
public class LevelChunkTranslator extends PacketTranslator<LevelChunkPacket> {
	private static final Registry<Biome> BIOMES_REGISTRY = BuiltinRegistries.BIOME;
	private final List<LevelChunkPacket> chunksOutOfRenderDistance = new ArrayList<>();

	public LevelChunkTranslator() {
		TunnelMC.instance.eventManager.registerListeners(this, this);
	}

	@Override
	public void translate(LevelChunkPacket packet) {
		int chunkX = packet.getChunkX();
		int chunkZ = packet.getChunkZ();

		if (TunnelMC.mc.player != null) {
			if (this.isChunkInRenderDistance(chunkX, chunkZ)) {
				this.chunksOutOfRenderDistance.add(packet);
				return;
			}
		}

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
			Client.instance.javaConnection.processServerToClientPacket(chunkDeltaUpdateS2CPacket);
		};

		if (TunnelMC.mc.world != null) {
			runnable.run();
		} else {
			MinecraftClient.getInstance().execute(runnable);
		}
	}

	@Override
	public Class<LevelChunkPacket> getPacketClass() {
		return LevelChunkPacket.class;
	}

	public boolean isChunkInRenderDistance(int chunkX, int chunkZ) {
		if (TunnelMC.mc.player == null) {
			return false;
		}
		int playerChunkX = MathHelper.floor(TunnelMC.mc.player.getX()) >> 4;
		int playerChunkZ = MathHelper.floor(TunnelMC.mc.player.getZ()) >> 4;
		return Math.abs(chunkX - playerChunkX) > TunnelMC.mc.options.getViewDistance().getValue() || Math.abs(chunkZ - playerChunkZ) > TunnelMC.mc.options.getViewDistance().getValue();
	}

	@Listener
	public void onEvent(EventPlayerTick event) {
		// This needs some work, general chunk loading needs some work as well.
		Iterator<LevelChunkPacket> iterator = chunksOutOfRenderDistance.iterator();
		while (iterator.hasNext()) {
			LevelChunkPacket chunk = iterator.next();
			if (this.isChunkInRenderDistance(chunk.getChunkX(), chunk.getChunkZ())) {
				continue;
			}

			translate(chunk);
			iterator.remove();
		}
	}
}
