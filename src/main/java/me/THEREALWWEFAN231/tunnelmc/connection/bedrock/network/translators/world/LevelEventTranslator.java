package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.google.common.collect.Sets;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinClientPlayerInteractionManager;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinWorldRenderer;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockPaletteTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.SortedSet;

@PacketIdentifier(LevelEventPacket.class)
public class LevelEventTranslator extends PacketTranslator<LevelEventPacket> {
    public static final Object2ObjectMap<Vector3i, BlockBreakingWrapper> BLOCK_BREAKING_INFOS = new Object2ObjectOpenHashMap<>();
    public static final LongSet TO_REMOVE = new LongOpenHashSet();

    @Override
    public void translate(LevelEventPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().interactionManager == null) {
            return;
        }

        switch (packet.getType()) {
            case BLOCK_START_BREAK -> {
                Vector3i position = packet.getPosition().toInt();
                BlockBreakingInfo blockBreakingInfo = new BlockBreakingInfo(0, PositionUtils.toBlockPos(position));
                BlockBreakingWrapper blockBreakingWrapper = new BlockBreakingWrapper(packet.getData(), blockBreakingInfo);
                BLOCK_BREAKING_INFOS.put(position, blockBreakingWrapper);
                SortedSet<BlockBreakingInfo> sortedSet = Sets.newTreeSet();
                sortedSet.add(blockBreakingInfo);
                ((IMixinWorldRenderer) MinecraftClient.getInstance().worldRenderer).getBlockBreakingProgressions().put(
                        BlockPos.asLong(position.getX(), position.getY(), position.getZ()), sortedSet);
            }
            case BLOCK_UPDATE_BREAK -> {
                BlockBreakingWrapper blockBreakingWrapper = BLOCK_BREAKING_INFOS.get(packet.getPosition().toInt());
                if (blockBreakingWrapper == null) {
                    break;
                }
                blockBreakingWrapper.length = packet.getData();
            }
            case BLOCK_STOP_BREAK -> {
                if (packet.getPosition().equals(Vector3f.ZERO)) {
                    if (BLOCK_BREAKING_INFOS.containsKey(packet.getPosition().toInt())) {
                        long key = ((IMixinClientPlayerInteractionManager) MinecraftClient.getInstance().interactionManager).getCurrentBreakingPos().asLong();
                        TO_REMOVE.add(key);
                    }
                } else {
                    Vector3i position = packet.getPosition().toInt();
                    if (BLOCK_BREAKING_INFOS.containsKey(position)) {
                        long key = BlockPos.asLong(position.getX(), position.getY(), position.getZ());
                        TO_REMOVE.add(key);
                    }
                }
            }
            case PARTICLE_CRACK_BLOCK -> {
                Direction direction = Direction.byId(packet.getData() >> 24);
                int bedrockRuntimeId = packet.getData() & 0xffffff; // Strip out the above encoding
                BlockState blockState = BlockPaletteTranslator.RUNTIME_ID_TO_BLOCK_STATE.get(bedrockRuntimeId);
                Vector3i vector = packet.getPosition().toInt();
                BlockPos pos = PositionUtils.toBlockPos(vector);
                TunnelMC.mc.world.setBlockState(pos, blockState);
//                TunnelMC.mc.particleManager.addBlockBreakParticles(pos, blockState); TODO
                TunnelMC.mc.particleManager.addBlockBreakingParticles(pos, direction);
            }
            case PARTICLE_DESTROY_BLOCK -> {
                MinecraftClient.getInstance().world.syncWorldEvent(MinecraftClient.getInstance().player, 2001,
                        PositionUtils.toBlockPos(packet.getPosition().toInt()),
                        Block.getRawIdFromState(BlockPaletteTranslator.RUNTIME_ID_TO_BLOCK_STATE.get(packet.getData())));
            }
        }
    }

    public static class BlockBreakingWrapper {
        public long lastUpdate;
        public int length;
        public float currentDuration;
        public BlockBreakingInfo blockBreakingInfo;

        public BlockBreakingWrapper(int length, BlockBreakingInfo blockBreakingInfo) {
            this.length = length;
            this.blockBreakingInfo = blockBreakingInfo;
        }
    }
}
