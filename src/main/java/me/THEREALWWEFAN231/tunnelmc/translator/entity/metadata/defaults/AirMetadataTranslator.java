package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityDataIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataPair;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslator;

@EntityDataIdentifier(EntityData.AIR_SUPPLY)
public class AirMetadataTranslator implements EntityMetadataTranslator<Short> {
    @Override
    public void translate(EntityMetadataPair<Short> data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        data.getEntity().setAir(data.getValue());
    }
}
