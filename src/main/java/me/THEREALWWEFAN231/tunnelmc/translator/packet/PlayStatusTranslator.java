package me.THEREALWWEFAN231.tunnelmc.translator.packet;

import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslator;

public class PlayStatusTranslator extends PacketTranslator<PlayStatusPacket> {
    @Override
    public void translate(PlayStatusPacket packet) {
        if(packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(Client.instance.entityRuntimeId);
            Client.instance.sendPacketImmediately(setLocalPlayerAsInitializedPacket);
        }
    }

    @Override
    public Class<PlayStatusPacket> getPacketClass() {
        return PlayStatusPacket.class;
    }
}
