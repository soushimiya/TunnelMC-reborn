package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent2Packet;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;

@PacketIdentifier(LevelSoundEvent2Packet.class)
public class LevelSoundEvent2Translator extends PacketTranslator<LevelSoundEvent2Packet> { // TODO: remove this?

    @Override
    public void translate(LevelSoundEvent2Packet packet, Client client) {
        System.out.println(packet);
        // Make this JSON mappings for any non-extra-data
    }
}
