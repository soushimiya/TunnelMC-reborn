package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.TranslatorManager;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class EntityMetadataTranslatorManager extends TranslatorManager<EntityMetadataTranslator<?>, EntityDataMap> {
    private final Map<EntityData, EntityMetadataTranslator<?>> translatorsByEntityDataKey = new HashMap<>();

    @Override
    protected void addTranslator(EntityMetadataTranslator<?> translator) {
        if(!translator.getClass().isAnnotationPresent(EntityDataIdentifier.class)) {
            log.warn("Skipping translator due to not having an annotation: " + translator.getClass().getSimpleName());
            return;
        }

        EntityDataIdentifier identifier = translator.getClass().getAnnotation(EntityDataIdentifier.class);
        this.translatorsByEntityDataKey.put(identifier.value(), translator);
    }

    @Override
    public void translateData(EntityDataMap data, BedrockConnection bedrockConnection, FakeJavaConnection connection) {
        for(EntityData entityData : data.keySet()) {
            EntityMetadataTranslator<?> translator = this.translatorsByEntityDataKey.get(entityData);
            if (translator == null) {
                return;
            }

            try {
                translator.translateType(data.ensureAndGet(entityData), bedrockConnection, connection);
            } catch (Throwable throwable) {
                log.error(throwable);
            }
        }
    }
}
