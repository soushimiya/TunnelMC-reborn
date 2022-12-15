package me.THEREALWWEFAN231.tunnelmc.mixins;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Log4j2
@Mixin(ClientConnection.class)
public class MixinClientConnection {
	@Shadow private Channel channel;
	@Shadow private Text disconnectReason;

	@Inject(method = "isOpen", at = @At("HEAD"), cancellable = true)
	public void isOpen(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		callbackInfoReturnable.setReturnValue(true);
	}

	@Inject(method = "isEncrypted", at = @At("HEAD"), cancellable = true)
	public void isEncrypted(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {//this allows player skins to be seen in the PlayerListHud
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		callbackInfoReturnable.setReturnValue(true);
	}

	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}
		BedrockConnectionAccessor.getCurrentConnection().handleJavaPacket(packet);
		ci.cancel();
	}

	@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
	public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback) {
		if (this.channel.isOpen()) {
			if (packet instanceof ParticleS2CPacket || packet instanceof QueryResponseS2CPacket || packet instanceof QueryPongS2CPacket) {
				return;
			}
			
			log.debug("Received: " + packet.getClass());
		}
	}

	@Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
	public void disconnect(Text disconnectReason, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		// this.channel is null here
		BedrockConnectionAccessor.closeConnection();
		this.disconnectReason = disconnectReason;
		ci.cancel();
	}
}
