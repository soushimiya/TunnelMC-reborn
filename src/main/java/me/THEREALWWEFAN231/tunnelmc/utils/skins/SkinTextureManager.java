package me.THEREALWWEFAN231.tunnelmc.utils.skins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import it.unimi.dsi.fastutil.Pair;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OpenGL doesn't like overlapping texture ids. Closing and (re-)registering on the same id doesn't work either.
 * So I have to make the registering static...
 */
@Log4j2
@UtilityClass
public class SkinTextureManager {
    private final Map<UUID, Pair<SerializedSkin, Integer>> serializedSkins = new HashMap<>();

    private Identifier getIdentifier(MinecraftProfileTexture.Type type, UUID uuid, int version) {
        String string = switch (type) {
            case SKIN -> "skins";
            case CAPE -> "capes";
            case ELYTRA -> "elytra";
        };
        return new Identifier(string + "/" + uuid.toString() + "/v" + version);
    }

    public Identifier getTexturePart(MinecraftProfileTexture.Type type, UUID uuid) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return null;
        }

        Pair<SerializedSkin, Integer> serializedSkin = serializedSkins.getOrDefault(uuid, null);
        if(serializedSkin == null) {
            return null;
        }

        Identifier identifier = getIdentifier(type, uuid, serializedSkin.second());
        AbstractTexture texture = TunnelMC.mc.getTextureManager().getOrDefault(identifier, null);
        if(texture == null) {
            ImageData imageData = switch (type) {
                case SKIN -> serializedSkin.first().getSkinData();
                case CAPE -> serializedSkin.first().getCapeData();
                case ELYTRA -> ImageData.EMPTY;
            };
            if(imageData.equals(ImageData.EMPTY)) {
                return null;
            }

            texture = new ImageDataPlayerSkinTexture(imageData, DefaultSkinHelper.getTexture(), MinecraftProfileTexture.Type.SKIN == type, null);

            TunnelMC.mc.getTextureManager().registerTexture(identifier, texture);
        }
        return identifier;
    }

    public void addSerializedSkin(UUID uuid, SerializedSkin skin) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }

        if(!skin.getGeometryName().equals("geometry.humanoid.custom") && !skin.getGeometryName().equals("geometry.humanoid.customSlim")) {
            log.warn("Discarding unknown geometry skin: {}", skin.getGeometryName());
            return;
        }

        serializedSkins.compute(uuid, (uuid1, pair) -> {
            int version = 0;
            if(pair != null) {
                version = pair.second() + 1;
            }

            return Pair.of(skin, version);
        });
    }
}
