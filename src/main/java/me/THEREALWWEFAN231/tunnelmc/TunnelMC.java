package me.THEREALWWEFAN231.tunnelmc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.nukkitx.api.event.EventManager;
import com.nukkitx.event.SimpleEventManager;
import lombok.Getter;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityRegistry;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.enchantment.EnchantmentTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.EntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.json.ItemStackSerializer;
import me.THEREALWWEFAN231.tunnelmc.utils.json.OAuth2AccessTokenDeserializer;
import me.THEREALWWEFAN231.tunnelmc.utils.json.OAuth2AccessTokenSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

import java.nio.file.Path;

public class TunnelMC implements ClientModInitializer {
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
			.registerModule(new SimpleModule("TunnelMC")
					.addSerializer(OAuth2AccessToken.class, new OAuth2AccessTokenSerializer())
					.addDeserializer(OAuth2AccessToken.class, new OAuth2AccessTokenDeserializer())
					.addSerializer(ItemStack.class, new ItemStackSerializer()))
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	public static final FabricAudiences ADVENTURE = FabricClientAudiences.of();
	public static final MinecraftClient mc = MinecraftClient.getInstance();

	@Getter
	private static TunnelMC instance;

	@Getter
	private EventManager eventManager;
	@Getter
	private Path configPath;

	public void onInitializeClient() {
		instance = this;
		this.eventManager = new SimpleEventManager();
		this.configPath = FabricLoader.getInstance().getConfigDir().resolve("tunnelmc");
		this.configPath.toFile().mkdirs();

		BlockEntityRegistry.load();
		EntityTranslator.load();
		ItemTranslator.load();
		EnchantmentTranslator.load();
		ScreenHandlerTranslatorManager.load();
	}
}
