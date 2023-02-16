package me.THEREALWWEFAN231.tunnelmc.gui;

import lombok.Getter;
import lombok.Setter;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OfflineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OnlineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.SavedLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class BedrockLoggingInScreen extends Screen {
    @Getter @Setter
    private Text status = Text.empty();
    private CompletableFuture<ChainData> future;
    private final Screen parent;
    private final File rememberAccountFile;
    private final BiConsumer<ChainData, Throwable> whenComplete;
    private final List<Element> removableElements = new ArrayList<>();

    public BedrockLoggingInScreen(Screen parent, MinecraftClient client, File rememberAccountFile, BiConsumer<ChainData, Throwable> whenComplete) {
        super(NarratorManager.EMPTY);
        this.client = client;
        this.parent = parent;
        this.whenComplete = whenComplete;
        this.rememberAccountFile = rememberAccountFile;
    }

    protected void init() {
        if (this.client == null) {
            return;
        }
        this.future = new OnlineModeLoginChainSupplier(this::setStatus, this.rememberAccountFile).get();
        this.future.whenComplete(this.whenComplete);

        int width = 200;
        int x = this.width / 2 - 100;
        if(this.rememberAccountFile != null && this.rememberAccountFile.exists()) {
            width = 99;
            x = this.width / 2 + 2;

            this.removableElements.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, width, 20, Text.of("Saved account"), (buttonWidget) -> {
                this.removeElements();

                MutableText previousStatus = this.getStatus().copy();
                this.setStatus(Text.of("Using saved account. Please wait..."));
                new SavedLoginChainSupplier(this.rememberAccountFile).get()
                        .whenComplete((chainData, throwable) -> {
                            if(throwable != null) {
                                this.setStatus(previousStatus.append("\n").append(Text.of(throwable.getMessage())));
                                return;
                            }

                            this.future.completeExceptionally(new CancellationException());
                        })
                        .whenComplete(this.whenComplete);
            })));
        }
        this.removableElements.add(this.addDrawableChild(new ButtonWidget(x, this.height / 4 + 96 + 12, width, 20, Text.of("Offline account"), (buttonWidget) -> {
            this.removeElements();

            MutableText previousStatus = this.getStatus().copy();
            this.setStatus(Text.of("Using offline account. Please wait..."));
            new OfflineModeLoginChainSupplier(TunnelMC.mc.getSession().getUsername()).get()
                    .whenComplete((chainData, throwable) -> {
                        if(throwable != null) {
                            this.setStatus(previousStatus.append("\n").append(Text.of(throwable.getMessage())));
                            return;
                        }

                        this.future.completeExceptionally(new CancellationException());
                    }).whenComplete(this.whenComplete);
        })));

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.future.completeExceptionally(new CancellationException());
            this.client.setScreen(this.parent);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int firstLineWidth = TunnelMC.mc.textRenderer.getWidth(this.getStatus().getString().split("\n")[0]);
        int textY = this.height / 2 - 75;

        int i = 0;
        for(OrderedText text : TunnelMC.mc.textRenderer.wrapLines(this.getStatus(), Math.max(this.width / 2, firstLineWidth))) {
            drawCenteredTextWithShadow(matrices, this.textRenderer, text, this.width / 2, textY + (i++ * (TunnelMC.mc.textRenderer.fontHeight + 1)), 0xFF_FF_FF);
        }

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
        int textY = this.height / 2 - 75;
        if(mouseY > textY && mouseY < textY + this.textRenderer.fontHeight) {
            Style style = this.getTextComponentUnderMouse((int)mouseX);
            if(style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                Util.getOperatingSystem().open(style.getClickEvent().getValue());
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(this.future != null) {
            this.future.completeExceptionally(new CancellationException());
        }
        this.removableElements.clear();
        this.init(client, width, height);
    }

    private void removeElements() {
        for(Element element : this.removableElements) {
            this.remove(element);
        }
    }

    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.getStatus().getContent() == TextContent.EMPTY) {
            return null;
        }
        int i = TunnelMC.mc.textRenderer.getWidth(this.getStatus().getString().split("\n")[0]);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        if (mouseX < j || mouseX > k) {
            return null;
        }
        return TunnelMC.mc.textRenderer.getTextHandler().getStyleAt(this.getStatus(), mouseX - j);
    }
}