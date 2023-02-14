package me.THEREALWWEFAN231.tunnelmc.mixins.serverlist;

import com.google.common.net.HostAndPort;
import me.THEREALWWEFAN231.tunnelmc.gui.list.BedrockServerInfo;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
	@Shadow private ServerInfo selectedEntry;

	@Shadow protected abstract void addEntry(boolean confirmedAction);

	protected MixinMultiplayerScreen(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At(value = "RETURN"))
	public void init(CallbackInfo callback) {
		this.addDrawableChild(new ButtonWidget(5, 5, 150, 20, Text.of("Add a Bedrock Server"), buttonWidget -> {
			this.selectedEntry = new BedrockServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
		}));
	}

	@Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;parse(Ljava/lang/String;)Lnet/minecraft/client/network/ServerAddress;"), cancellable = true)
	public void connect(ServerInfo entry, CallbackInfo ci) {
		if(!(entry instanceof BedrockServerInfo)) {
			return;
		}
		ci.cancel();

		HostAndPort address = HostAndPort.fromString(entry.address).withDefaultPort(19132);
		ConnectScreen.connect(this, this.client, new ServerAddress(address.getHost(), address.getPort()), entry);
	}
}
