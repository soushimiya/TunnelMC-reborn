package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityDataIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataPair;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslator;
import net.minecraft.text.Text;

@EntityDataIdentifier(EntityData.NAMETAG)
public class NametagMetadataTranslator implements EntityMetadataTranslator<String> {
    @Override
    public void translate(EntityMetadataPair<String> data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        data.getEntity().setCustomName(Text.of(data.getValue()));
    }
}
