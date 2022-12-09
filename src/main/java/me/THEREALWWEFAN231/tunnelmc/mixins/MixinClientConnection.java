package me.THEREALWWEFAN231.tunnelmc.mixins;

import net.minecraft.network.PacketCallbacks;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
	@Shadow private Channel channel;
	@Shadow private Text disconnectReason;

	@Inject(method = "isOpen", at = @At("HEAD"), cancellable = true)
	public void isOpen(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (Client.instance.isConnectionOpen()) {
			callbackInfoReturnable.setReturnValue(true);
		}
	}

	@Inject(method = "isEncrypted", at = @At("HEAD"), cancellable = true)
	public void isEncrypted(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {//this allows player skins to be seen in the PlayerListHud
		if (Client.instance.isConnectionOpen()) {
			callbackInfoReturnable.setReturnValue(true);
		}
	}

	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
		if (Client.instance.isConnectionOpen()) {
			Client.instance.javaConnection.packetTranslatorManager.translatePacket(packet);
			ci.cancel();
		}
	}

	@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
	public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback) {
		if (this.channel.isOpen()) {
			if (packet instanceof ParticleS2CPacket || packet instanceof QueryResponseS2CPacket || packet instanceof QueryPongS2CPacket) {
				return;
			}
			
			System.out.println("got packet " + packet.getClass());
		}
	}

	@Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
	public void disconnect(Text disconnectReason, CallbackInfo ci) {
		if (Client.instance.isConnectionOpen()) {
			// this.channel is null here
			Client.instance.bedrockClient.close(true);
			this.disconnectReason = disconnectReason;
			ci.cancel();
		}
	}

}
