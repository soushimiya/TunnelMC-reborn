package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import it.unimi.dsi.fastutil.Pair;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

@PacketIdentifier(SetEntityDataPacket.class)
public class SetEntityDataTranslator extends PacketTranslator<SetEntityDataPacket> {

	@Override
	public void translate(SetEntityDataPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int id = (int) packet.getRuntimeEntityId();

		TunnelMC.mc.executeSync(() -> {
			Entity entity = TunnelMC.mc.world.getEntityById(id);
			if (entity == null) {
				return;
			}
			bedrockConnection.getEntityMetadataTranslatorManager().translateData(Pair.of(entity, packet.getMetadata()), bedrockConnection, javaConnection);

			EntityTrackerUpdateS2CPacket trackerUpdatePacket = new EntityTrackerUpdateS2CPacket(id, entity.getDataTracker(), true);
			javaConnection.processJavaPacket(trackerUpdatePacket);
		});
	}
}
