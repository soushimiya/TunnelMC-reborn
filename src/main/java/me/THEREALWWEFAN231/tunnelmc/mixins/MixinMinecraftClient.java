package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.nukkitx.protocol.bedrock.packet.InteractPacket;
import com.nukkitx.protocol.bedrock.packet.InteractPacket.Action;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.events.GameTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	private long ticks;

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		TunnelMC.getInstance().getEventManager().fire(new GameTickEvent(ticks++));
	}

	@Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V"))
	public void onBlockBreaking(ParticleManager particleManager, BlockPos pos, Direction direction) {
		if (!BedrockConnectionAccessor.isConnectionOpen()) {
			// Don't let the client add this - let the server
			particleManager.addBlockBreakingParticles(pos, direction);
		}
	}

	//inventory opened, I could have sworn there was some packet for this(that could be translated) I can't find it, I am so confused, found it!!! ClientCommandC2SPacket ClientCommandC2SPacket.Mode.OPEN_INVENTORY, packet might only be sent when the player is riding an entity/
	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	private void handleInputEvents(CallbackInfo callbackInfo) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		InteractPacket interactPacket = new InteractPacket();
		interactPacket.setAction(Action.OPEN_INVENTORY);
		interactPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());

		BedrockConnectionAccessor.getCurrentConnection().sendPacket(interactPacket);
	}
}
