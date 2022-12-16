package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.utils.ImageDataPlayerSkinTexture;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.Base64;
import java.util.Objects;

@Log4j2
@Mixin(PlayerSkinProvider.class)
public abstract class MixinPlayerSkinProvider {
    @Shadow private static Identifier getSkinId(MinecraftProfileTexture.Type skinType, String hash) {
        return null;
    }

    @Shadow @Final private TextureManager textureManager;
    @Shadow @Final private File skinCacheDir;

    /**
     * @author Flonja
     * @reason Bypassing the need for a skin url
     */
    @Overwrite
    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = getSkinId(type, string);
        AbstractTexture abstractTexture = this.textureManager.getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);

            Runnable skinCallback = () -> {
                if (callback != null) {
                    callback.onSkinTextureAvailable(type, identifier, profileTexture);
                }
            };

            AbstractTexture playerSkinTexture;
            if(BedrockConnectionAccessor.isConnectionOpen()) {
                int width = Integer.parseInt(Objects.requireNonNull(
                        profileTexture.getMetadata("tunnelmc:width")));
                int height = Integer.parseInt(Objects.requireNonNull(
                        profileTexture.getMetadata("tunnelmc:height")));
                byte[] image = Base64.getDecoder().decode(Objects.requireNonNull(
                        profileTexture.getMetadata("tunnelmc:data")).getBytes());
                ImageData imageData = ImageData.of(width, height, image);

                playerSkinTexture = new ImageDataPlayerSkinTexture(imageData, DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, skinCallback);
            }else{
                playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, skinCallback);
            }

            this.textureManager.registerTexture(identifier, playerSkinTexture);
        } else if (callback != null) {
            callback.onSkinTextureAvailable(type, identifier, profileTexture);
        }

        return identifier;
    }
}
