package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import com.nukkitx.api.event.Listener;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.GameType;
import com.nukkitx.protocol.bedrock.data.PacketCompressionAlgorithm;
import com.nukkitx.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OfflineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.OnlineModeLoginChainSupplier;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.AuthData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.BedrockPacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.ClientBatchHandler;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.BlockEntityDataCache;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerInitializedEvent;
import me.THEREALWWEFAN231.tunnelmc.events.SessionInitializedEvent;
import me.THEREALWWEFAN231.tunnelmc.gui.BedrockConnectingScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.text.Text;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class BedrockConnection {
	public static final BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;

	@Getter
	private final InetSocketAddress targetAddress;
	private final PacketTranslatorManager<BedrockPacket> packetTranslatorManager;
	final BedrockClient bedrockClient;
	private FakeJavaConnection javaConnection;

	@Getter
	private ChainData chainData;
	@Getter
	private AuthData authData;
	private BedrockConnectingScreen connectScreen;

	@Getter
	private BedrockContainers wrappedContainers;
	@Getter
	private BlockEntityDataCache blockEntityDataCache;

	public int entityRuntimeId;
	public AuthoritativeMovementMode movementMode = AuthoritativeMovementMode.CLIENT;
	public GameType defaultGameMode;
	public final Map<UUID, String> displayNames = new HashMap<>();
	public final Map<String, UUID> profileNameToUuid = new HashMap<>();
	public final AtomicBoolean startedSprinting = new AtomicBoolean();
	public final AtomicBoolean startedSneaking = new AtomicBoolean();
	public final AtomicBoolean stoppedSprinting = new AtomicBoolean();
	public final AtomicBoolean stoppedSneaking = new AtomicBoolean();
	public final AtomicBoolean jumping = new AtomicBoolean();

	BedrockConnection(InetSocketAddress bindAddress, InetSocketAddress targetAddress) {
		this.bedrockClient = new BedrockClient(bindAddress);
		this.bedrockClient.setRakNetVersion(BedrockConnection.CODEC.getRaknetProtocolVersion());
		this.bedrockClient.bind().join();
		this.packetTranslatorManager = new BedrockPacketTranslatorManager();
		this.targetAddress = targetAddress;

		TunnelMC.getInstance().getEventManager().registerListeners(this, this);
	}

	public void connect(boolean onlineMode) {
		this.connectScreen = new BedrockConnectingScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), BedrockConnectionAccessor::closeConnection);
		TunnelMC.mc.setScreen(this.connectScreen);

		LoginChainSupplier supplier;
		if (onlineMode) {
			supplier = new OnlineModeLoginChainSupplier(s -> this.connectScreen.setStatus(Text.of(s)));
		} else {
			supplier = new OfflineModeLoginChainSupplier(TunnelMC.mc.getSession().getUsername());
		}

		supplier.get().whenComplete((chainData, throwable) -> {
			if(throwable != null) {
				BedrockConnectionAccessor.closeConnection(throwable);
				return;
			}

			this.chainData = chainData;
			this.authData = this.chainData.decodeAuthData();
			this.connectScreen.setStatus(Text.translatable("connect.connecting"));

			this.bedrockClient.connect(this.targetAddress).whenComplete((session, throwable1) -> {
				if (throwable1 != null) {
					BedrockConnectionAccessor.closeConnection(throwable1);
					return;
				}

				this.connectScreen.setStatus(Text.of("Logging in..."));
				TunnelMC.getInstance().getEventManager().fire(new SessionInitializedEvent(session));
			});
		});
	}

	public void sendPacketImmediately(BedrockPacket packet) {
		BedrockSession session = this.bedrockClient.getSession();

		if (session != null) {
			session.sendPacketImmediately(packet);
			if (session.isLogging()) {
				log.info("Outbound {}: {}", session.getAddress().toString(), packet.getClass().getCanonicalName());
			}
		}
	}

	public void handleJavaPacket(Packet<?> packet) {
		this.javaConnection.translatePacket(packet);
	}

	public void sendPacket(BedrockPacket packet) {
		BedrockSession session = this.bedrockClient.getSession();

		if (session != null) {
			session.sendPacket(packet);
			if (session.isLogging()) {
				log.info("Outbound {}: {}", session.getAddress().toString(), packet.getClass().getCanonicalName());
			}
		}
	}

	public void setHardcodedBlockingId(int id) {
		System.out.println(this.bedrockClient.getSession().getHardcodedBlockingId().get());
		if(!this.bedrockClient.getSession().getHardcodedBlockingId().compareAndSet(-1, id)) {
			throw new IllegalStateException("Blocking id is already set");
		}
	}

	public void setCompressionMethod(PacketCompressionAlgorithm compressionAlgorithm) {
		this.bedrockClient.getSession().setCompression(compressionAlgorithm);
	}

	public void enableEncryption(SecretKey key) {
		if(this.bedrockClient.getSession().isEncrypted()) {
			throw new IllegalStateException("Connection is already encrypted");
		}
		this.bedrockClient.getSession().enableEncryption(key);
	}

	@Listener
	public void onEvent(SessionInitializedEvent event) {
		BedrockSession bedrockSession = event.getSession();
		FakeJavaConnection javaConnection = new FakeJavaConnection(this);

		bedrockSession.setPacketCodec(BedrockConnection.CODEC);
		bedrockSession.addDisconnectHandler(reason -> MinecraftClient.getInstance().execute(() -> {
			// We disconnected ourselves.
			if (reason == DisconnectReason.DISCONNECTED) {
				return;
			}

			BedrockConnectionAccessor.closeConnection("You were disconnected from the target server because: " + reason.toString());
		}));

		bedrockSession.setBatchHandler(new ClientBatchHandler(this, javaConnection, this.packetTranslatorManager));
		bedrockSession.setLogging(false);

		try {
			RequestNetworkSettingsPacket packet = new RequestNetworkSettingsPacket();
			packet.setProtocolVersion(BedrockConnection.CODEC.getProtocolVersion());
			this.sendPacketImmediately(packet);

			this.connectScreen.setStatus(Text.of("Loading resources..."));
			this.javaConnection = javaConnection;
		} catch (Exception e) {
			BedrockConnectionAccessor.closeConnection(e);
		}
	}

	@Listener
	public void onEvent(PlayerInitializedEvent event) {
		this.wrappedContainers = new BedrockContainers();
		this.blockEntityDataCache = new BlockEntityDataCache();
	}
}