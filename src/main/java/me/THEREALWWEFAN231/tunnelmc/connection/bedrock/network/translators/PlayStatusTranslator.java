package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.*;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

import java.time.Instant;

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
                bedrockConnection.spawned();
            }
            case LOGIN_FAILED_CLIENT_OLD -> BedrockConnectionAccessor.closeConnection("Tell the developer to update the mod!");
            case LOGIN_FAILED_SERVER_OLD -> BedrockConnectionAccessor.closeConnection("Server is outdated.");
            case LOGIN_FAILED_EDITION_MISMATCH_VANILLA_TO_EDU ->
                    BedrockConnectionAccessor.closeConnection("TunnelMC cannot join Education Edition servers");
            default -> BedrockConnectionAccessor.closeConnection(packet.getStatus().name());
        }
    }
}
