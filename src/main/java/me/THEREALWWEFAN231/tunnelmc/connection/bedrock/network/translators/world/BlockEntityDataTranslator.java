package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityRegistry;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtil;
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
    public void translate(BlockEntityDataPacket packet, Client client) {
        BlockEntityTranslator translator = BlockEntityRegistry.getBlockEntityTranslator(packet.getData());
        if (translator != null) {
            NbtCompound tag = translator.translateTag(packet.getData());
            try {
                BlockEntityUpdateS2CPacket updatePacket = constructor.newInstance(
                        PositionUtil.toBlockPos(packet.getBlockPosition()), translator.getJavaId(), tag);
                client.javaConnection.processServerToClientPacket(updatePacket);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
