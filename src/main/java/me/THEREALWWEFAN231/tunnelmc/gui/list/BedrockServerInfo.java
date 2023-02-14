package me.THEREALWWEFAN231.tunnelmc.gui.list;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class BedrockServerInfo extends ServerInfo {

    public BedrockServerInfo(String name, String address, boolean local) {
        super(name, address, local);
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();
        nbt.putBoolean("bedrock", true);
        return nbt;
    }

    public static BedrockServerInfo fromNbt(NbtCompound root) {
        BedrockServerInfo serverInfo = new BedrockServerInfo(root.getString("name"), root.getString("ip"), false);
        if (root.contains("icon", NbtElement.STRING_TYPE)) {
            serverInfo.setIcon(root.getString("icon"));
        }
        if (root.contains("acceptTextures", NbtElement.BYTE_TYPE)) {
            if (root.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.ENABLED);
            } else {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.DISABLED);
            }
        } else {
            serverInfo.setResourcePackPolicy(ResourcePackPolicy.PROMPT);
        }
        return serverInfo;
    }
}
