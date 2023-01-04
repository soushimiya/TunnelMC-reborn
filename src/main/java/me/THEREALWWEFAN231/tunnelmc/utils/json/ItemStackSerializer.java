package me.THEREALWWEFAN231.tunnelmc.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class ItemStackSerializer extends StdSerializer<ItemStack> {
    public ItemStackSerializer() {
        super(ItemStack.class);
    }

    @Override
    public void serialize(ItemStack value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("item", value.getItem().getName().getString());
        gen.writeNumberField("count", value.getCount());
        gen.writeNumberField("damage", value.getDamage());
        gen.writePOJOField("nbt", value.getNbt());
        gen.writeEndObject();
    }
}
