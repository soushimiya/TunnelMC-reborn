package me.THEREALWWEFAN231.tunnelmc.mixins.serverlist;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.gui.BedrockLoggingInScreen;
import me.THEREALWWEFAN231.tunnelmc.gui.list.BedrockServerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.CancellationException;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.getRandomPort;

@Mixin(ConnectScreen.class)
public abstract class MixinConnectScreen extends Screen {
	@Shadow @Final static Logger LOGGER;
	@Shadow volatile boolean connectingCancelled;
	@Shadow @Final Screen parent;

	@Shadow protected abstract void setStatus(Text status);

	protected MixinConnectScreen(Text title) {
		super(title);
	}

	@Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V", at = @At(value = "HEAD"), cancellable = true)
	public void connect(MinecraftClient client, ServerAddress address, CallbackInfo ci) {
		ServerInfo serverInfo = this.client.getCurrentServerEntry();
		if(!(serverInfo instanceof BedrockServerInfo)) {
			return;
		}
		ci.cancel();
		LOGGER.info("Connecting to {}, {}", address.getAddress(), address.getPort());

		File tokenFile = TunnelMC.getInstance().getConfigPath().resolve("bedrock.tok").toFile();
		this.client.setScreen(new BedrockLoggingInScreen(this.parent, this.client, tokenFile, (chainData, throwable) -> {
			if(throwable != null) {
				if(!(throwable instanceof CancellationException)) {
					LOGGER.error("Got error when getting chain data", throwable);
				}
				return;
			}
			if(this.connectingCancelled) {
				return;
			}

			BedrockConnection connection = BedrockConnectionAccessor.createNewConnection(
					new InetSocketAddress("0.0.0.0", getRandomPort()),
					new InetSocketAddress(address.getAddress(), address.getPort()));
			connection.connect(chainData, this::setStatus, () -> {
				if(this.connectingCancelled) {
					BedrockConnectionAccessor.closeConnection();
					this.client.setScreen(this.parent);
				}
				return this.connectingCancelled;
			});
		}));
	}
}
