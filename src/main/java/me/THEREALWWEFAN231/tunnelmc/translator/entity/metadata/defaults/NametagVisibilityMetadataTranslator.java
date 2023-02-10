package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityDataIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataPair;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslator;

@EntityDataIdentifier(EntityData.NAMETAG_ALWAYS_SHOW)
public class NametagVisibilityMetadataTranslator implements EntityMetadataTranslator<Byte> {
    @Override
    public void translate(EntityMetadataPair<Byte> data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        data.getEntity().setCustomNameVisible(data.getValue() == 1);
    }
}
