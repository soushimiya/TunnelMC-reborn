package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.utils.LevelChunkDecoder;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.tick.ChunkTickScheduler;

import java.io.IOException;
import java.util.Objects;

/**
 * For more information, check out the following gist:
 * <a href="https://gist.github.com/dktapps/8a4f23d2bf32ea7091ef14e4aac46170">Block Changes in Beta 1.2.13</a>
 */
@Log4j2
@PacketIdentifier(LevelChunkPacket.class)
public class LevelChunkTranslator extends PacketTranslator<LevelChunkPacket> {
	private static final Registry<Biome> BIOMES_REGISTRY = BuiltinRegistries.BIOME;

	@Override
	public void translate(LevelChunkPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int chunkX = packet.getChunkX();
		int chunkZ = packet.getChunkZ();

		ChunkSection[] chunkSections = new ChunkSection[24];

		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(packet.getData());

		for (int sectionIndex = 0; sectionIndex < packet.getSubChunksLength(); sectionIndex++) {
			ChunkSection chunkSection = new ChunkSection(sectionIndex, BIOMES_REGISTRY);
			int chunkVersion = byteBuf.readByte();
			if (chunkVersion != 1 && chunkVersion != 8 && chunkVersion != 9) {
//				System.out.println("Decoding a version zero chunk...");
				LevelChunkDecoder.networkDecodeVersionZero(byteBuf, chunkSection);
			} else if (chunkVersion == 1) {
//				System.out.println("Decoding a version one chunk...");
				LevelChunkDecoder.networkDecodeVersionOne(byteBuf, chunkSection);
			} else if (chunkVersion == 8){
//				System.out.println("Decoding a version eight chunk...");
				LevelChunkDecoder.networkDecodeVersionEight(byteBuf, chunkSection, byteBuf.readByte());
			} else {
//				System.out.println("Decoding a version nine chunk...");
				LevelChunkDecoder.networkDecodeVersionNine(byteBuf, chunkSection, byteBuf.readByte());
			}
			chunkSections[sectionIndex] = chunkSection;
		}

		ReadableContainer<RegistryEntry<Biome>> last = null;
		for (int sectionIndex = 0; sectionIndex < packet.getSubChunksLength(); sectionIndex++) {
			ReadableContainer<RegistryEntry<Biome>> biomes = LevelChunkDecoder.biomeDecodingFromPalette(byteBuf, BIOMES_REGISTRY);
			if(biomes == null) {
				if(sectionIndex == 0) {
					throw new IllegalStateException("Cannot use last palette at index 0");
				}

				biomes = last;
			}else{
				last = biomes;
			}

			ChunkSection section = chunkSections[sectionIndex];
			if(section == null) {
				throw new IllegalStateException("Should exist");
			}

			PacketByteBuf buf = PacketByteBufs.create();
			biomes.writePacket(buf);

			PalettedContainer<RegistryEntry<Biome>> container = (PalettedContainer<RegistryEntry<Biome>>) section.getBiomeContainer();
			container.readPacket(buf);
		}
		byte borderBlocks = byteBuf.readByte();
		for (int entry = 0; entry < borderBlocks; entry++) {
			byteBuf.readByte(); // Useless data for us
		}

		log.debug("start print");
		while (byteBuf.isReadable()) {
			try(NBTInputStream nbtStream = NbtUtils.createNetworkReader(new ByteBufInputStream(byteBuf))) {
				log.debug(nbtStream.readTag());
			}catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		log.debug("end print");

		Runnable runnable = () -> {
			WorldChunk worldChunk = new WorldChunk(Objects.requireNonNull(TunnelMC.mc.world), new ChunkPos(chunkX, chunkZ), UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler<>(), new ChunkTickScheduler<>(), 0, chunkSections, null, null);

			ChunkDataS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDataS2CPacket(worldChunk, TunnelMC.mc.world.getLightingProvider(), null, null, true);
			javaConnection.processJavaPacket(chunkDeltaUpdateS2CPacket);
		};

		if (TunnelMC.mc.world != null) {
			runnable.run();
		} else {
			MinecraftClient.getInstance().execute(runnable);
		}
	}
}
