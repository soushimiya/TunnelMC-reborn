package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslatorManager.NO_AI;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        ((IMixinEntity) this).getDataTracker().startTracking(NO_AI, false);
    }
}
