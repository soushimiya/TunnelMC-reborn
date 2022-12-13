package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    public Object renderCrosshair(SimpleOption<AttackIndicator> instance) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return instance.getValue();
        }

        return AttackIndicator.OFF;
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    public Object renderHotbar(SimpleOption<AttackIndicator> instance) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return instance.getValue();
        }

        return AttackIndicator.OFF;
    }
}
