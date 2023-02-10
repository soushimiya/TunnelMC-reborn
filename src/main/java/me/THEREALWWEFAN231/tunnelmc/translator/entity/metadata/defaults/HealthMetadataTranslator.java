package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityDataIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataPair;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@EntityDataIdentifier(EntityData.HEALTH)
public class HealthMetadataTranslator implements EntityMetadataTranslator<Integer> {
    @Override
    public void translate(EntityMetadataPair<Integer> data, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        Entity entity = data.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setHealth(data.getValue());
        }
    }
}
