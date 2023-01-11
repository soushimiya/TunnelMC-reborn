package me.THEREALWWEFAN231.tunnelmc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@UtilityClass
public class FileUtils {

	public JsonNode getJsonFromResource(String resourceName) throws IOException {
		InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resourceName);
		if (inputStream == null) {
			throw new FileNotFoundException("Resource \"" + resourceName + "\" does not exist!");
		}

		return JSON_MAPPER.readTree(inputStream);
	}
}