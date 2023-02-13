package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import it.unimi.dsi.fastutil.Pair;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.utils.ImageDataPlayerSkinTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {

    @Shadow public abstract GameProfile getProfile();

    private Pair<SerializedSkin, Integer> getSerializedSkin() {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return null;
        }
        return BedrockConnectionAccessor.getCurrentConnection().getSerializedSkin(getProfile().getId());
    }

    private Identifier getIdentifier(MinecraftProfileTexture.Type skinType, int version) {
        String string = switch (skinType) {
            case SKIN -> "skins";
            case CAPE -> "capes";
            case ELYTRA -> "elytra";
        };
        return new Identifier(string + "/" + getProfile().getId().toString() + "/v" + version);
    }

    @Inject(method = "getSkinTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        Pair<SerializedSkin, Integer> serializedSkin = getSerializedSkin();
        if(serializedSkin == null) {
            return;
        }

        Identifier identifier = getIdentifier(MinecraftProfileTexture.Type.SKIN, serializedSkin.second());
        AbstractTexture texture = TunnelMC.mc.getTextureManager().getOrDefault(identifier, null);
        if(texture == null) {
            ImageData imageData = serializedSkin.first().getSkinData();
            texture = new ImageDataPlayerSkinTexture(imageData, DefaultSkinHelper.getTexture(), true, null);

            TunnelMC.mc.getTextureManager().registerTexture(identifier, texture);
        }
        cir.setReturnValue(identifier);
    }

    @Inject(method = "getCapeTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        Pair<SerializedSkin, Integer> serializedSkin = getSerializedSkin();
        if(serializedSkin == null) {
            return;
        }

        Identifier identifier = getIdentifier(MinecraftProfileTexture.Type.CAPE, serializedSkin.second());
        AbstractTexture texture = TunnelMC.mc.getTextureManager().getOrDefault(identifier, null);
        if(texture == null) {
            ImageData imageData = serializedSkin.first().getCapeData();
            texture = new ImageDataPlayerSkinTexture(imageData, DefaultSkinHelper.getTexture(), false, null);

            TunnelMC.mc.getTextureManager().registerTexture(identifier, texture);
        }
        cir.setReturnValue(identifier);
    }

    @Inject(method = "getElytraTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getElytraTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        cir.setReturnValue(null); // doesn't exist on bedrock
    }
}
