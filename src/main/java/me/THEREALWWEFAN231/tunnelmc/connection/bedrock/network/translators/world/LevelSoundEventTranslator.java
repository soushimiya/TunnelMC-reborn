package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEventPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockPaletteTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

@PacketIdentifier(LevelSoundEventPacket.class)
public class LevelSoundEventTranslator extends PacketTranslator<LevelSoundEventPacket> {

    @Override
    public void translate(LevelSoundEventPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        if (packet.getSound() == SoundEvent.HIT) {
            BlockPos pos = PositionUtils.toBlockPos(packet.getPosition());
            BlockState blockState = BlockPaletteTranslator.RUNTIME_ID_TO_BLOCK_STATE.get(packet.getExtraData());
            BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
            MinecraftClient.getInstance().getSoundManager().play(
                    new PositionedSoundInstance(blockSoundGroup.getHitSound(), SoundCategory.BLOCKS,
                            (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, SoundInstance.createRandom(), pos));
        }
    }
}
