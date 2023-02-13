package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.utils.skins.SkinTextureManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {

    @Shadow public abstract GameProfile getProfile();

    private Identifier getTexturePart(MinecraftProfileTexture.Type type) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return null;
        }
        return SkinTextureManager.getTexturePart(type, getProfile().getId());
    }

    @Inject(method = "getSkinTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.SKIN));
    }

    @Inject(method = "getCapeTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.CAPE));
    }

    @Inject(method = "getElytraTexture", at = @At(value = "HEAD"), cancellable = true)
    public void getElytraTexture(CallbackInfoReturnable<Identifier> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        cir.setReturnValue(null); // doesn't exist on bedrock
    }
}
