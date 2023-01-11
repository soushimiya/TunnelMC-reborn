package me.THEREALWWEFAN231.tunnelmc.translator.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinNbtCompound;
import me.THEREALWWEFAN231.tunnelmc.translator.blockstate.BlockPaletteTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.enchantment.EnchantmentTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.*;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@Log4j2
public class ItemTranslator {
	//key, is the item id:damage, so for example could be 218:13
	public static final HashBiMap<String, Item> BEDROCK_ITEM_INFO_TO_JAVA_ITEM = HashBiMap.create();
	public static final List<Item> BLOCKED_ITEMS = new ArrayList<>();

	static {  // TODO: make public static fields read only
		try {
			ObjectNode itemsObject = (ObjectNode) FileUtils.getJsonFromResource("geyser/items.json");
			ArrayNode statesArray = (ArrayNode) FileUtils.getJsonFromResource("geyser/runtime_item_states.json");
			ArrayNode overrideTranslations = (ArrayNode) FileUtils.getJsonFromResource("tunnel/item_override_translations.json");

			HashMap<String, Integer> identifierToIntId = new HashMap<>();
			for(JsonNode jsonElement : statesArray) {
				identifierToIntId.put(jsonElement.get("name").asText(), jsonElement.get("id").asInt());
			}

			for (Iterator<Map.Entry<String, JsonNode>> it = itemsObject.fields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				String javaStringIdentifier = entry.getKey();
				Identifier javaIdentifier = new Identifier(javaStringIdentifier);
				Item item = Registry.ITEM.get(javaIdentifier);

				for(JsonNode blockedItem : overrideTranslations) {
					if(blockedItem.asText().equals(javaStringIdentifier)) {
						BLOCKED_ITEMS.add(item);
					}
				}
				if(BLOCKED_ITEMS.contains(item)) {
					continue;
				}

				JsonNode bedrockItemData = entry.getValue();
				String bedrockIdentifier = bedrockItemData.get("bedrock_identifier").asText();
				int bedrockId = identifierToIntId.get(bedrockIdentifier);
				int bedrockData = bedrockItemData.get("bedrock_data").asInt();

				if (item == Items.AIR && !javaStringIdentifier.equals("minecraft:air")) {//item not found
					log.error(javaStringIdentifier + " item was not found, this generally isn't good.");
					continue;
				}

				BEDROCK_ITEM_INFO_TO_JAVA_ITEM.put(bedrockId + ":" + bedrockData, item);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO: tags and what ever
	public static ItemStack itemDataToItemStack(ItemData itemData) {
		//keep the short cast, the server can send us non-short numbers that, "need to be rolled over" to their correct id
		Item item = BEDROCK_ITEM_INFO_TO_JAVA_ITEM.get((short) itemData.getId() + ":" + itemData.getDamage());
		if(item == null) {
			Item defaultItem = BEDROCK_ITEM_INFO_TO_JAVA_ITEM.get((short) itemData.getId() + ":0");
			if(defaultItem == null) {
				return ItemStack.EMPTY;
			}

			item = defaultItem;
		}

		ItemStack itemStack = new ItemStack(item);
		itemStack.setCount(itemData.getCount());
		if(itemStack.isDamageable()) {
			itemStack.setDamage(itemData.getDamage());
		}

		if (itemData.getTag() != null) {
			itemStack.setNbt(convertBedrockToJavaTags(itemData.getTag()));
			itemStack.setDamage(itemData.getTag().getInt("Damage", itemData.getDamage()));
			itemStack.setCustomName(Text.literal(itemData.getTag().getCompound("display").getString("Name", null)));

			List<NbtMap> bedrockEnchantments = itemData.getTag().getList("ench", NbtType.COMPOUND, null);
			if (bedrockEnchantments != null) {
				for(NbtMap enchantmentData : bedrockEnchantments) {
					int bedrockEnchantmentId = enchantmentData.getShort("id");
					int enchantmentLevel = enchantmentData.getShort("lvl");
					
					Enchantment javaEnchantment = EnchantmentTranslator.BEDROCK_TO_JAVA_ENCHANTMENTS.get(bedrockEnchantmentId);
					if(javaEnchantment == null) {
						log.error("Enchantment " + bedrockEnchantmentId + " not found");
						continue;
					}

					itemStack.addEnchantment(javaEnchantment, enchantmentLevel);
				}
			}
		}

		return itemStack;
	}

	//TODO: tags and what ever
	public static ItemData itemStackToItemData(ItemStack itemStack) {
		if(!BEDROCK_ITEM_INFO_TO_JAVA_ITEM.containsValue(itemStack.getItem())) {
			throw new RuntimeException("Cannot find java item: " + itemStack.getItem().getName().getString());
		}
		String idDamageString = BEDROCK_ITEM_INFO_TO_JAVA_ITEM.inverse().get(itemStack.getItem());
		String[] idDamageSplit = idDamageString.split(":");

		int blockRuntimeId = BlockPaletteTranslator.BLOCK_STATE_TO_RUNTIME_ID.getOrDefault(Block.getBlockFromItem(itemStack.getItem()).getDefaultState(), 0);
		return ItemData.builder()
				.id(Integer.parseInt(idDamageSplit[0]))
				.damage(Integer.parseInt(idDamageSplit[1]))
				.count(itemStack.getCount())
				.tag(convertJavaToBedrockTags(itemStack.getNbt()))
				.blockRuntimeId(blockRuntimeId)
				.build();
	}

	private static NbtMap convertJavaToBedrockTags(NbtCompound root) {
		if(root == null) {
			return null;
		}

		NbtMapBuilder builder = NbtMap.builder();

		try {
			byte[] flattened = JSON_MAPPER.writeValueAsBytes(((IMixinNbtCompound) root).getEntries());
			Map<String, Object> map = JSON_MAPPER.readValue(flattened, new TypeReference<>() {});
			builder.putAll(map);
		} catch (IOException e) {
			log.catching(e);
		}

		return builder.build();
	}

	private static NbtCompound convertBedrockToJavaTags(NbtMap root) {
		try {
			return StringNbtReader.parse(root.toString());
		} catch (CommandSyntaxException e) {
			log.catching(e);
		}

		return new NbtCompound();
	}
}
