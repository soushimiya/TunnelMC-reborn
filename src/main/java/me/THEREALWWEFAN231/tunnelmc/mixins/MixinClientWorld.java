package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Shadow @Final private ClientWorld.Properties clientWorldProperties;

    @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
    public void setTimeOfDay(long l, CallbackInfo ci) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }
        // Don't allow the gamerule to be overwritten
        this.clientWorldProperties.setTimeOfDay(l);
        ci.cancel();
    }
}
