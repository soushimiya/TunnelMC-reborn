package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerTickEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
	@Shadow public Input input;
	private long ticks;

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"))
	public void tick(CallbackInfo callbackInfo) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		TunnelMC.getInstance().getEventManager().fire(new PlayerTickEvent(ticks++));
	}

	@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V"))
	public void tickMovement(CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		BedrockConnectionAccessor.getCurrentConnection().jumping.set(input.jumping);
	}
}
