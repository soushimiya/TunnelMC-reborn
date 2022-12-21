package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "isIn", at = @At("RETURN"), cancellable = true)
    public void isIn(ItemGroup group, CallbackInfoReturnable<Boolean> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen() || !cir.getReturnValue()) {
            return;
        }

        cir.setReturnValue(!ItemTranslator.BLOCKED_ITEMS.contains((Item) (Object) this));
    }
}
