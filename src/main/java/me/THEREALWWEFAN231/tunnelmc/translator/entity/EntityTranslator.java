package me.THEREALWWEFAN231.tunnelmc.translator.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import java.util.*;

@Log4j2
public class EntityTranslator {
	public static final HashMap<String, EntityType<?>> BEDROCK_IDENTIFIER_TO_ENTITY_TYPE = new HashMap<>();

	public static void load() {
		List<EntityType<?>> allEntityTypes = Registry.ENTITY_TYPE.stream().toList();

		for (EntityType<?> e : allEntityTypes) {
			BEDROCK_IDENTIFIER_TO_ENTITY_TYPE.put(EntityType.getId(e).toString(), e);
		}

		ObjectNode jsonObject = (ObjectNode) FileUtils.getJsonFromResource("tunnel/entity_override_translations.json");
		if (jsonObject == null) {
			return;
		}

		for (Iterator<Map.Entry<String, JsonNode>> it = jsonObject.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			Optional<EntityType<?>> optional = EntityType.get(entry.getValue().asText());
			if (optional.isEmpty()) {
				log.debug("Could not find entity type " + entry.getValue().asText() + " when reading entity_override_translations.json");
				continue;
			}

			BEDROCK_IDENTIFIER_TO_ENTITY_TYPE.put(entry.getKey(), optional.get());
		}
	}
}