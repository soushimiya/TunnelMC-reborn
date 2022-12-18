package me.THEREALWWEFAN231.tunnelmc.gui;

import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OfflineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;

@Log4j2
@Environment(EnvType.CLIENT)
public class BedrockConnectionScreen extends Screen {

	//	The UI that comes up after you click on Connect To Bedrock.

	private ButtonWidget joinServerButton;
	private TextFieldWidget addressField;
	private TextFieldWidget portField;
	private CheckboxWidget onlineModeWidget;
	private CheckboxWidget rememberAccountWidget;
	private final Screen parent;

	public BedrockConnectionScreen(Screen parent) {
		super(Text.of("Bedrock Connection"));
		this.parent = parent;
	}

	public void init() {
		if (this.client == null) {
			return;
		}
		this.client.keyboard.setRepeatEvents(true);
		this.joinServerButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 100 + 12, 204, 20, Text.translatable("selectServer.select"), button -> {
			if (this.addressField.getText().isEmpty()) {
				return;
			}

			int port = 19132;
			try {
				port = Integer.parseInt(this.portField.getText());
			} catch (NumberFormatException ignore) {}

			BedrockConnection connection = BedrockConnectionAccessor.createNewConnection(
					new InetSocketAddress("0.0.0.0", getRandomPort()),
					new InetSocketAddress(this.addressField.getText(), port));
			BiConsumer<ChainData, Throwable> whenComplete = (chainData, throwable) -> {
				if(throwable != null && !(throwable instanceof CancellationException)) {
					log.error("Got error when getting chain data", throwable);
					return;
				}

				connection.connect(chainData, this);
			};

			if (this.onlineModeWidget.isChecked()) {
				File tokenFile = TunnelMC.getInstance().getConfigPath().resolve("bedrock.tok").toFile();
				this.client.setScreen(new BedrockLoggingInScreen(this, this.client, this.rememberAccountWidget.isChecked() ? tokenFile : null, whenComplete));
				return;
			}

			new OfflineModeLoginChainSupplier(TunnelMC.mc.getSession().getUsername())
					.get().whenComplete(whenComplete);
		}));

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 125 + 12, 204, 20, ScreenTexts.CANCEL, button -> BedrockConnectionScreen.this.client.setScreen(BedrockConnectionScreen.this.parent)));
		this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, (this.height / 4) + 16, 200, 20, Text.of("Enter IP"));
		this.portField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, (this.height / 4) + 46, 200, 20, Text.of("Enter Port"));
		this.onlineModeWidget = new CheckboxWidget(this.width / 2 - 100, (this.height / 4) + 80, 100, 20, Text.of("Online mode"), true);
		this.rememberAccountWidget = new CheckboxWidget(this.width / 2, (this.height / 4) + 80, 100, 20, Text.of("Remember Account"), true);
		this.addressField.setMaxLength(128);
		this.portField.setMaxLength(6);
		this.addressField.setTextFieldFocused(true);
		this.portField.setTextFieldFocused(false);
		this.addressField.setText("127.0.0.1");
		this.portField.setText("19132");
		this.addressField.setChangedListener(text -> BedrockConnectionScreen.this.onAddressFieldChanged());
		this.addDrawableChild(this.addressField);
		this.addDrawableChild(this.portField);
		this.setInitialFocus(this.addressField);
		this.onAddressFieldChanged();
	}

	public void tick() {
		this.addressField.tick();
		this.portField.tick();
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		Screen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		Screen.drawTextWithShadow(matrices, this.textRenderer, Text.of("Enter IP and Port"), this.width / 2 - 100, this.height / 4, 10526880);
		this.addressField.render(matrices, mouseX, mouseY, delta);
		this.portField.render(matrices, mouseX, mouseY, delta);
		this.onlineModeWidget.render(matrices, mouseX, mouseY, delta);
		if(this.onlineModeWidget.isChecked()) {
			this.rememberAccountWidget.render(matrices, mouseX, mouseY, delta);
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.addressField.mouseClicked(mouseX, mouseY, button);
		this.portField.mouseClicked(mouseX, mouseY, button);
		this.onlineModeWidget.mouseClicked(mouseX, mouseY, button);
		if(this.onlineModeWidget.isChecked()) {
			this.rememberAccountWidget.mouseClicked(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if ((this.addressField.isFocused() || this.portField.isFocused()) && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
			this.joinServerButton.onPress();
			this.joinServerButton.playDownSound(this.client.getSoundManager());
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void resize(MinecraftClient client, int width, int height) {
		String addressText = this.addressField.getText();
		String portText = this.portField.getText();
		this.init(client, width, height);
		this.addressField.setText(addressText);
		this.portField.setText(portText);
	}

	public void onClose() {
		this.client.setScreen(this.parent);
	}

	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
		this.client.options.write();
	}

	private void onAddressFieldChanged() {
		String addressText = this.addressField.getText();
		this.joinServerButton.active = !addressText.isEmpty() && addressText.split(":").length > 0 && addressText.indexOf(32) == -1;
	}

	private static int getRandomPort() {
		try (DatagramSocket datagramSocket = new DatagramSocket(0)) {
			return datagramSocket.getLocalPort();
		} catch(SocketException e) {
			throw new RuntimeException("Could not open socket to find next free port", e);
		}
	}
}