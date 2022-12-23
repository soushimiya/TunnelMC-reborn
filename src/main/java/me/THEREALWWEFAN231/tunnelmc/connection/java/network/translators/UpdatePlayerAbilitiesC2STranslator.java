package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.Ability;
import com.nukkitx.protocol.bedrock.data.AbilityType;
import com.nukkitx.protocol.bedrock.packet.RequestAbilityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;

@PacketIdentifier(UpdatePlayerAbilitiesC2SPacket.class)
public class UpdatePlayerAbilitiesC2STranslator extends PacketTranslator<UpdatePlayerAbilitiesC2SPacket> {

    @Override
    public void translate(UpdatePlayerAbilitiesC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        RequestAbilityPacket pk = new RequestAbilityPacket();
        pk.setAbility(Ability.FLYING);
        pk.setType(AbilityType.BOOLEAN);
        pk.setBoolValue(packet.isFlying());

        bedrockConnection.sendPacket(pk);
    }
}
