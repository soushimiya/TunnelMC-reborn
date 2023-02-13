package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.*;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerSpawnedEvent;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinPlayerEntity;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.time.Instant;
import java.util.Collections;

@PacketIdentifier(PlayStatusPacket.class)
public class PlayStatusTranslator extends PacketTranslator<PlayStatusPacket> {
    @Override
    public void translate(PlayStatusPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        switch (packet.getStatus()) {
            case LOGIN_SUCCESS -> {
                bedrockConnection.sendPacketImmediately(new ClientCacheStatusPacket());
                bedrockConnection.expect(ResourcePacksInfoPacket.class);
            }
            case PLAYER_SPAWN -> {
                TickSyncPacket tickSyncPacket = new TickSyncPacket();
                tickSyncPacket.setRequestTimestamp(Instant.now().toEpochMilli());
                bedrockConnection.sendPacketImmediately(tickSyncPacket);

                SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
                setLocalPlayerAsInitializedPacket.setRuntimeEntityId(bedrockConnection.runtimeId);
                bedrockConnection.sendPacketImmediately(setLocalPlayerAsInitializedPacket);
                TunnelMC.getInstance().getEventManager().fire(new PlayerSpawnedEvent());

                javaConnection.processJavaPacket(new PlayerPositionLookS2CPacket(
                        bedrockConnection.spawnLocation.getX(),
                        bedrockConnection.spawnLocation.getY(),
                        bedrockConnection.spawnLocation.getZ(),
                        bedrockConnection.spawnRotation.getX(),
                        bedrockConnection.spawnRotation.getY(),
                        Collections.emptySet(), 1, false));

                // TODO: respect the client settings
                TunnelMC.mc.executeSync(() -> {
                    TunnelMC.mc.player.getDataTracker().set(IMixinPlayerEntity.PLAYER_MODEL_PARTS(), (byte) (PlayerModelPart.JACKET.getBitFlag()
                            | PlayerModelPart.HAT.getBitFlag()
                            | PlayerModelPart.LEFT_SLEEVE.getBitFlag()
                            | PlayerModelPart.LEFT_PANTS_LEG.getBitFlag()
                            | PlayerModelPart.RIGHT_SLEEVE.getBitFlag()
                            | PlayerModelPart.RIGHT_PANTS_LEG.getBitFlag()
                            | PlayerModelPart.CAPE.getBitFlag()));
                    EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket = new EntityTrackerUpdateS2CPacket((int) bedrockConnection.runtimeId, TunnelMC.mc.player.getDataTracker(), true);
                    javaConnection.processJavaPacket(entityTrackerUpdateS2CPacket);
                });
            }
            case LOGIN_FAILED_CLIENT_OLD -> BedrockConnectionAccessor.closeConnection("Tell the developer to update the mod!");
            case LOGIN_FAILED_SERVER_OLD -> BedrockConnectionAccessor.closeConnection("Server is outdated.");
            case LOGIN_FAILED_EDITION_MISMATCH_VANILLA_TO_EDU ->
                    BedrockConnectionAccessor.closeConnection("TunnelMC cannot join Education Edition servers");
            default -> BedrockConnectionAccessor.closeConnection(packet.getStatus().name());
        }
    }
}
