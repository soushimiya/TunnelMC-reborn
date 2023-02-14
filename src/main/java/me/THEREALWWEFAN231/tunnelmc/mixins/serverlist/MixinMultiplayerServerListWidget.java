package me.THEREALWWEFAN231.tunnelmc.mixins.serverlist;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nukkitx.protocol.bedrock.BedrockClient;
import lombok.extern.slf4j.Slf4j;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.gui.list.BedrockServerInfo;
import net.minecraft.GameVersion;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.getRandomPort;

@Slf4j
@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinMultiplayerServerListWidget {
    private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL =
            new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder()
                    .setNameFormat("Server Pinger #%d")
                    .setDaemon(true)
                    .setUncaughtExceptionHandler(new UncaughtExceptionLogger(log))
                    .build());
    private static final Text CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve")
            .formatted(Formatting.DARK_RED);
    private static final Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect")
            .formatted(Formatting.DARK_RED);

    @Shadow @Final private ServerInfo server;
    @Shadow public abstract void saveFile();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"), cancellable = true)
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if(!(this.server instanceof BedrockServerInfo)) {
            return;
        }
        ci.cancel();

        HostAndPort address = HostAndPort.fromString(this.server.address).withDefaultPort(19132);
        InetSocketAddress socketAddress = new InetSocketAddress(address.getHost(), address.getPort());
        SERVER_PINGER_THREAD_POOL.submit(() -> {
            this.server.label = Text.translatable("multiplayer.status.pinging");
            this.server.ping = -1L;
            this.server.playerListSummary = null;

            long currTime = System.currentTimeMillis();

            BedrockClient bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", getRandomPort()));
            bedrockClient.setRakNetVersion(BedrockConnection.CODEC.getRaknetProtocolVersion());
            bedrockClient.bind().join();
            bedrockClient.ping(socketAddress).whenComplete((pong, throwable) -> {
                if(throwable != null) {
                    if(throwable instanceof UnknownHostException) {
                        this.server.ping = -1L;
                        this.server.label = CANNOT_RESOLVE_TEXT;
                        return;
                    }
                    this.server.ping = -1L;
                    this.server.label = CANNOT_CONNECT_TEXT;
                    return;
                }

                this.server.online = true;
                this.server.label = Text.of(pong.getMotd() + "\n" + pong.getSubMotd());
                this.server.ping = System.currentTimeMillis() - currTime;
                this.server.playerCountLabel = createPlayerCountText(pong.getPlayerCount(), pong.getMaximumPlayerCount());
                this.server.version = Text.of(pong.getVersion());
                this.server.protocolVersion = BedrockConnection.CODEC.getProtocolVersion(); // I want to show the player count

                TunnelMC.mc.execute(this::saveFile);
            });
        });
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameVersion;getProtocolVersion()I"))
    public int checkGameVersion(GameVersion instance) {
        if(!(this.server instanceof BedrockServerInfo)) {
            return instance.getProtocolVersion();
        }
        return BedrockConnection.CODEC.getProtocolVersion();
    }

    private static Text createPlayerCountText(int current, int max) {
        if(current >= 1000) {
            return Text.literal(Integer.toString(current)).formatted(Formatting.GRAY); // TODO: make this a toggleable setting, but still keep it a fun nod.
        }

        return Text.literal(Integer.toString(current)).append(Text.literal("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(max)).formatted(Formatting.GRAY);
    }
}
