package me.THEREALWWEFAN231.tunnelmc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.api.event.EventManager;
import com.nukkitx.event.SimpleEventManager;
import me.THEREALWWEFAN231.tunnelmc.translator.EntityTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.PacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.blockentity.BlockEntityRegistry;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockStateTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.enchantment.EnchantmentTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class TunnelMC implements ClientModInitializer {
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	public static TunnelMC instance;
	public static MinecraftClient mc = MinecraftClient.getInstance();

	public EventManager eventManager;
	public PacketTranslatorManager packetTranslatorManager;

	public void onInitializeClient() {
		instance = this;
		this.eventManager = new SimpleEventManager();
		this.packetTranslatorManager = new PacketTranslatorManager();

		BlockEntityRegistry.load();
		BlockStateTranslator.load();
		EntityTranslator.load();
		ItemTranslator.load();
		EnchantmentTranslator.load();
		ScreenHandlerTranslatorManager.load();
	}
}
