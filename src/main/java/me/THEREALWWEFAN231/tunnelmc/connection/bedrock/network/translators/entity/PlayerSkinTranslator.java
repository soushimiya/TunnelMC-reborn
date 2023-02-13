package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.packet.PlayerSkinPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerEntity;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

@PacketIdentifier(PlayerSkinPacket.class)
public class PlayerSkinTranslator extends PacketTranslator<PlayerSkinPacket> {

    @Override
    public void translate(PlayerSkinPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        bedrockConnection.addSerializedSkin(packet.getUuid(), packet.getSkin());

        TunnelMC.mc.executeSync(() -> {
            PlayerEntity player = TunnelMC.mc.world.getPlayerByUuid(packet.getUuid());
            if(player == null) {
                return;
            }

            byte capeBitFlag = (byte) (packet.getSkin().getCapeData().equals(ImageData.EMPTY) ? 0x0 : PlayerModelPart.CAPE.getBitFlag());
            player.getDataTracker().set(IMixinPlayerEntity.PLAYER_MODEL_PARTS(), (byte) (PlayerModelPart.JACKET.getBitFlag()
                    | PlayerModelPart.HAT.getBitFlag()
                    | PlayerModelPart.LEFT_SLEEVE.getBitFlag()
                    | PlayerModelPart.LEFT_PANTS_LEG.getBitFlag()
                    | PlayerModelPart.RIGHT_SLEEVE.getBitFlag()
                    | PlayerModelPart.RIGHT_PANTS_LEG.getBitFlag()
                    | capeBitFlag));
            EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket = new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker(), true);
            javaConnection.processJavaPacket(entityTrackerUpdateS2CPacket);
        });
    }
}
