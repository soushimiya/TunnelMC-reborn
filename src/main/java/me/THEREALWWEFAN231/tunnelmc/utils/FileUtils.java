package me.THEREALWWEFAN231.tunnelmc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@UtilityClass
public class FileUtils {

	public JsonNode getJsonFromResource(String resourceName) {
		InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resourceName);
		if (inputStream == null) {
			throw new RuntimeException("Resource \"" + resourceName + "\" does not exist!");
		}

		try {
			return JSON_MAPPER.readTree(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}