package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinEntity;
import me.THEREALWWEFAN231.tunnelmc.utils.skins.SkinTextureManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This is kept for backup if there isn't a PlayerListEntry
 */
@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {

	private Identifier getTexturePart(MinecraftProfileTexture.Type type) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return null;
		}
		return SkinTextureManager.getTexturePart(type, ((IMixinEntity) this).getUuid());
	}

	@Inject(method = "hasSkinTexture", at = @At(value = "HEAD"), cancellable = true)
	public void hasSkinTexture(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.SKIN) != null);
	}

	@Inject(method = "getSkinTexture", at = @At(value = "TAIL"), cancellable = true)
	public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
		if(!BedrockConnectionAccessor.isConnectionOpen() || cir.getReturnValue() != null) {
			return;
		}
		cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.SKIN));
	}

	@Inject(method = "canRenderCapeTexture", at = @At(value = "HEAD"), cancellable = true)
	public void canRenderCapeTexture(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.CAPE) != null);
	}

	@Inject(method = "getCapeTexture", at = @At(value = "TAIL"), cancellable = true)
	public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
		if(!BedrockConnectionAccessor.isConnectionOpen() || cir.getReturnValue() != null) {
			return;
		}
		cir.setReturnValue(this.getTexturePart(MinecraftProfileTexture.Type.CAPE));
	}

	@Inject(method = "canRenderElytraTexture", at = @At(value = "HEAD"), cancellable = true)
	public void canRenderElytraTexture(CallbackInfoReturnable<Boolean> cir) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		cir.setReturnValue(false); // doesn't exist on bedrock
	}
}
