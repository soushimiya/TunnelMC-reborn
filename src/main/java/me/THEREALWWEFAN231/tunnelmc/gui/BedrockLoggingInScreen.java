package me.THEREALWWEFAN231.tunnelmc.gui;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OnlineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.SavedLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Util;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class BedrockLoggingInScreen extends Screen {
    private Text status = Text.empty();
    private CompletableFuture<ChainData> future;
    private final Screen parent;
    private final File rememberAccountFile;
    private final BiConsumer<ChainData, Throwable> whenComplete;

    public BedrockLoggingInScreen(Screen parent, MinecraftClient client, File rememberAccountFile, BiConsumer<ChainData, Throwable> whenComplete) {
        super(NarratorManager.EMPTY);
        this.client = client;
        this.parent = parent;
        this.whenComplete = whenComplete;
        this.rememberAccountFile = rememberAccountFile;
    }

    public void setStatus(Text status) {
        this.status = status;
    }

    protected void init() {
        if (this.client == null) {
            return;
        }
        this.future = new OnlineModeLoginChainSupplier(this::setStatus, this.rememberAccountFile).get();
        this.future.whenComplete(this.whenComplete);

        if(this.rememberAccountFile != null && this.rememberAccountFile.exists()) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 100 + 12, 200, 20, Text.of("Use saved account"), (buttonWidget) -> {
                this.remove(buttonWidget);

                this.setStatus(Text.of("Using saved account. Please wait..."));
                new SavedLoginChainSupplier(this.rememberAccountFile).get()
                        .whenComplete((chainData, throwable) -> {
                            if(throwable != null) {
                                this.setStatus(Text.of(throwable.getMessage()));
                                return;
                            }

                            this.future.completeExceptionally(new CancellationException());
                        })
                        .whenComplete(this.whenComplete);
            }));
        }

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.future.completeExceptionally(new CancellationException());
            this.client.setScreen(this.parent);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int textY = this.height / 2 - 50;
        drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, textY, 0xFF_FF_FF);

        if(mouseY > textY && mouseY < textY + this.textRenderer.fontHeight) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            if (style != null && style.getHoverEvent() != null) {
                this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int textY = this.height / 2 - 50;
        if(mouseY > textY && mouseY < textY + this.textRenderer.fontHeight) {
            Style style = this.getTextComponentUnderMouse((int)mouseX);
            if(style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                Util.getOperatingSystem().open(style.getClickEvent().getValue());
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void resize(MinecraftClient client, int width, int height) {
        if(this.future != null) {
            this.future.completeExceptionally(new CancellationException());
        }
        this.init(client, width, height);
    }

    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.status.getContent() == TextContent.EMPTY) {
            return null;
        }
        int i = TunnelMC.mc.textRenderer.getWidth(this.status);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        if (mouseX < j || mouseX > k) {
            return null;
        }
        return TunnelMC.mc.textRenderer.getTextHandler().getStyleAt(this.status, mouseX - j);
    }
}