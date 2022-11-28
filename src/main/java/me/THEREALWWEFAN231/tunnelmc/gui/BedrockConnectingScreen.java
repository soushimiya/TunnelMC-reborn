package me.THEREALWWEFAN231.tunnelmc.gui;

import com.nukkitx.protocol.bedrock.BedrockClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BedrockConnectingScreen extends Screen {

    private Text status = Text.translatable("connect.connecting");
    private final BedrockClient connection;
    private final Screen parent;

    public BedrockConnectingScreen(Screen parent, MinecraftClient client, BedrockClient connection) {
        super(NarratorManager.EMPTY);
        this.client = client;
        this.parent = parent;
        this.connection = connection;
    }

    public void setStatus(Text status) {
        this.status = status;
    }

    protected void init() {
        if (this.client == null) {
            return;
        }

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            if (this.connection != null) {
                this.connection.close(true);
            }

            this.client.setScreen(this.parent);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

}