package me.THEREALWWEFAN231.tunnelmc.utils;

import com.google.gson.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileManagement {
	public Gson gJson = new GsonBuilder().disableHtmlEscaping().create();

	public String getTextFromInputStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}

		byteArrayOutputStream.close();
		inputStream.close();

		return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
	}

	public JsonElement getJsonFromResource(String resourceName) {
		InputStream inputStream = FileManagement.class.getClassLoader().getResourceAsStream(resourceName);
		if (inputStream == null) {
			System.out.println("Resource \"" + resourceName + "\" does not exist!");
			return null;
		}

		try {
			return JsonParser.parseString(this.getTextFromInputStream(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to read \"" + resourceName + "\": " + e.getMessage());
			return null;
		}
	}

}