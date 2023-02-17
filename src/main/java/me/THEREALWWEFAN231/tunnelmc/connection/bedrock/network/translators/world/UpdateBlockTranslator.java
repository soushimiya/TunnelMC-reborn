package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockPaletteTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockStateTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.PositionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

@Log4j2
@PacketIdentifier(UpdateBlockPacket.class)
public class UpdateBlockTranslator extends PacketTranslator<UpdateBlockPacket> {
	//TODO: Probably want to check out flags.
	
	@Override
	public void translate(UpdateBlockPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		BlockPos blockPos = PositionUtils.toBlockPos(packet.getBlockPosition());
		BlockState translatedBlockState = BlockStateTranslator.getBlockStateFromRuntimeId(packet.getRuntimeId());

		if (packet.getDataLayer() == 0) {
			BlockUpdateS2CPacket blockUpdateS2CPacket = new BlockUpdateS2CPacket(blockPos, translatedBlockState);
			javaConnection.processJavaPacket(blockUpdateS2CPacket);
		} else if (packet.getDataLayer() == 1) {
			// Set waterlogged state of existing block.
			BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				if (!blockState.contains(Properties.WATERLOGGED)) {
					return; // TODO: display block anyway (without collision of course)
				}

				translatedBlockState = blockState.with(Properties.WATERLOGGED, packet.getRuntimeId() == BlockPaletteTranslator.WATER_BEDROCK_BLOCK_ID);
			}

			BlockUpdateS2CPacket blockUpdateS2CPacket = new BlockUpdateS2CPacket(blockPos, translatedBlockState);
			javaConnection.processJavaPacket(blockUpdateS2CPacket);
		}
	}
}
