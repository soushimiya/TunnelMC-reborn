package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlags;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityDataIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataPair;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;

@EntityDataIdentifier(EntityData.FLAGS)
public class SneakingMetadataTranslator implements EntityMetadataTranslator<EntityFlags> {
    @Override
    public void translate(EntityMetadataPair<EntityFlags> data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        Entity entity = data.getEntity();
        boolean sneaking = data.getValue().getFlag(EntityFlag.SNEAKING);

        entity.setSneaking(sneaking);
        if (sneaking) {
            entity.setPose(EntityPose.CROUCHING);
        } else {
            entity.setPose(EntityPose.STANDING);
        }
    }
}
