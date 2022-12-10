package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityRegistry;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtils;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@PacketIdentifier(BlockEntityDataPacket.class)
public class BlockEntityDataTranslator extends PacketTranslator<BlockEntityDataPacket> {
    private final static Constructor<BlockEntityUpdateS2CPacket> constructor;

    static {
        try {
            constructor = BlockEntityUpdateS2CPacket.class.getDeclaredConstructor(BlockPos.class, BlockEntityType.class, NbtCompound.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void translate(BlockEntityDataPacket packet, BedrockConnection bedrockConnection) {
        Vector3i blockPosition = packet.getBlockPosition();
        NbtMap blockEntityData = packet.getData();
        bedrockConnection.blockEntityDataCache.getCachedBlockPositionsData().put(blockPosition, blockEntityData);

        BlockEntityTranslator translator = BlockEntityRegistry.getBlockEntityTranslator(packet.getData());
        if (translator != null) {
            NbtCompound tag = translator.translateTag(packet.getData());
            try {
                BlockEntityUpdateS2CPacket updatePacket = constructor.newInstance(
                        PositionUtils.toBlockPos(packet.getBlockPosition()), translator.getJavaId(), tag);
                bedrockConnection.javaConnection.processServerToClientPacket(updatePacket);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
