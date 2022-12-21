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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.SortedSet;

@PacketIdentifier(LevelEventPacket.class)
public class LevelEventTranslator extends PacketTranslator<LevelEventPacket> {
    public static final Object2ObjectMap<Vector3i, BlockBreakingWrapper> BLOCK_BREAKING_INFOS = new Object2ObjectOpenHashMap<>();
    public static final LongSet TO_REMOVE = new LongOpenHashSet();
    private static final Random random = Random.create();

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
                if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    Box box = blockState.getOutlineShape(TunnelMC.mc.world, pos).getBoundingBox();
                    double x1 = (double)x + random.nextDouble() * (box.maxX - box.minX - 0.20000000298023224) + 0.10000000149011612 + box.minX;
                    double y1 = (double)y + random.nextDouble() * (box.maxY - box.minY - 0.20000000298023224) + 0.10000000149011612 + box.minY;
                    double z1 = (double)z + random.nextDouble() * (box.maxZ - box.minZ - 0.20000000298023224) + 0.10000000149011612 + box.minZ;
                    switch (direction) {
                        case UP -> y1 = (double)y + box.maxY + 0.10000000149011612;
                        case DOWN -> y1 = (double)y + box.minY - 0.10000000149011612;
                        case NORTH -> z1 = (double)z + box.minZ - 0.10000000149011612;
                        case SOUTH -> z1 = (double)z + box.maxZ + 0.10000000149011612;
                        case WEST -> x1 = (double)x + box.minX - 0.10000000149011612;
                        case EAST -> x1 = (double)x + box.maxX + 0.10000000149011612;
                    }

                    TunnelMC.mc.particleManager.addParticle((new BlockDustParticle(TunnelMC.mc.world, x1, y1, z1, 0.0, 0.0, 0.0, blockState, pos)).move(0.2F).scale(0.6F));
                }
            }
            case PARTICLE_DESTROY_BLOCK -> MinecraftClient.getInstance().world.syncWorldEvent(MinecraftClient.getInstance().player, 2001,
                    PositionUtils.toBlockPos(packet.getPosition().toInt()),
                    Block.getRawIdFromState(BlockPaletteTranslator.RUNTIME_ID_TO_BLOCK_STATE.get(packet.getData())));
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
