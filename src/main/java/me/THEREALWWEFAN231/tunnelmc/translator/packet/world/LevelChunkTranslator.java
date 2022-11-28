package me.THEREALWWEFAN231.tunnelmc.translator.packet.world;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.events.EventPlayerTick;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * For more information, check out the following gist:
 * <a href="https://gist.github.com/dktapps/8a4f23d2bf32ea7091ef14e4aac46170">Block Changes in Beta 1.2.13</a>
 */
public class LevelChunkTranslator extends PacketTranslator<LevelChunkPacket> {

	private static final Registry<Biome> BIOMES_REGISTRY = BuiltinRegistries.BIOME;

	private final List<LevelChunkPacket> chunksOutOfRenderDistance = new ArrayList<>();

	public LevelChunkTranslator() {
		EventManager.register(this);
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

		ChunkSection[] chunkSections = new ChunkSection[16];

		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(packet.getData());

		for (int sectionIndex = 0; sectionIndex < packet.getSubChunksLength(); sectionIndex++) {
			chunkSections[sectionIndex] = new ChunkSection(sectionIndex, BIOMES_REGISTRY);
			int chunkVersion = byteBuf.readByte();
			if (chunkVersion != 1 && chunkVersion != 8) {
				System.out.println("Decoding a version zero chunk...");
				LevelChunkDecoder.networkDecodeVersionZero(byteBuf, chunkSections[sectionIndex]);
				continue;
			}
			if (chunkVersion == 1) {
				System.out.println("Decoding a version one chunk...");
				LevelChunkDecoder.networkDecodeVersionOne(byteBuf, chunkSections[sectionIndex]);
				continue;
			}

			System.out.println("Decoding a version eight chunk...");
			LevelChunkDecoder.networkDecodeVersionEight(byteBuf, chunkSections, sectionIndex, byteBuf.readByte());
		}

		byte[] bedrockBiomes = new byte[256];
		byteBuf.readBytes(bedrockBiomes);

		// TODO: Block entities

		int[] javaBiomes = new int[1024];
		int javaBiomesCount = 0;
		for (byte bedrockBiome : bedrockBiomes) {
			byte desiredBiome = bedrockBiome;

			if (BIOMES_REGISTRY.get(desiredBiome) == null) {
				desiredBiome = 1;
			}

			javaBiomes[javaBiomesCount++] = desiredBiome;
			javaBiomes[javaBiomesCount++] = desiredBiome;
			javaBiomes[javaBiomesCount++] = desiredBiome;
			javaBiomes[javaBiomesCount++] = desiredBiome;
		}

		WorldChunk worldChunk = new WorldChunk(null, new ChunkPos(chunkX, chunkZ), UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler(), new ChunkTickScheduler(), 0, chunkSections, null, null);

		for (int i = 0; i < worldChunk.getSectionArray().length; i++) {
			worldChunk.getSectionArray()[i] = chunkSections[i];
		}

		ChunkDataS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDataS2CPacket(worldChunk, new LightingProvider(new ChunkProvider() {
			@Nullable
			@Override
			public BlockView getChunk(int x, int z) {
				if(chunkX == x && chunkZ == z) {
					return worldChunk;
				}
				return null;
			}

			@Override
			public BlockView getWorld() {
				return null;
			}
		}, true, TunnelMC.mc.world.getDimension().hasSkyLight()), null, null, true);
		Client.instance.javaConnection.processServerToClientPacket(chunkDeltaUpdateS2CPacket);
	}

	@Override
	public Class<?> getPacketClass() {
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

	@EventTarget
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
