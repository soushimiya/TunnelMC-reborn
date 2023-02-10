package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults.ImmobileMetadataTranslator.NO_AI;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "isImmobile", at = @At(value = "TAIL"), cancellable = true)
    public void isImmobile(CallbackInfoReturnable<Boolean> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        if(cir.getReturnValue()) {
            return;
        }
        cir.setReturnValue(((IMixinEntity) this).getDataTracker().get(NO_AI));
    }
}
