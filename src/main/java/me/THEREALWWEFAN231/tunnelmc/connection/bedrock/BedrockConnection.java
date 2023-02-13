package me.THEREALWWEFAN231.tunnelmc.connection.bedrock;

import com.nukkitx.api.event.Listener;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.PacketCompressionAlgorithm;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.data.PlayerBlockActionData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.AuthData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ChainData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.BedrockPacketTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.ClientBatchHandler;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.BlockEntityDataCache;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerInitializedEvent;
import me.THEREALWWEFAN231.tunnelmc.events.PlayerSpawnedEvent;
import me.THEREALWWEFAN231.tunnelmc.events.SessionClosedEvent;
import me.THEREALWWEFAN231.tunnelmc.events.SessionInitializedEvent;
import me.THEREALWWEFAN231.tunnelmc.gui.BedrockConnectingScreen;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.EntityMetadataTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslatorManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.Packet;
import net.minecraft.text.Text;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class BedrockConnection {
	public static final BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;

	@Getter
	private final InetSocketAddress targetAddress;
	private final PacketTranslatorManager<BedrockPacket> packetTranslatorManager;
	private final BedrockClient bedrockClient;
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
	@Getter
	private final EntityMetadataTranslatorManager entityMetadataTranslatorManager = new EntityMetadataTranslatorManager();

	public long runtimeId;
	public long uniqueId;
	public Vector3f spawnLocation;
	public Vector2f spawnRotation;
	public AuthoritativeMovementMode movementMode = AuthoritativeMovementMode.CLIENT;
	private final Map<UUID, Pair<SerializedSkin, Integer>> serializedSkins = new HashMap<>();
	private final List<Class<? extends BedrockPacket>> expectedPackets = new ArrayList<>();
	private final AtomicBoolean spawned = new AtomicBoolean(false);
	public final AtomicBoolean jumping = new AtomicBoolean();
	public final Set<PlayerAuthInputData> authInputData = new HashSet<>();
	public final List<PlayerBlockActionData> blockActions = new ArrayList<>();

	BedrockConnection(InetSocketAddress bindAddress, InetSocketAddress targetAddress) {
		this.bedrockClient = new BedrockClient(bindAddress);
		this.bedrockClient.setRakNetVersion(BedrockConnection.CODEC.getRaknetProtocolVersion());
		this.bedrockClient.bind().join();
		this.packetTranslatorManager = new BedrockPacketTranslatorManager(this);
		this.targetAddress = targetAddress;

		TunnelMC.getInstance().getEventManager().registerListeners(this, this);
	}

	public void connect(ChainData chainData, Screen parent) {
		this.connectScreen = new BedrockConnectingScreen(parent, MinecraftClient.getInstance(), BedrockConnectionAccessor::closeConnection);
		TunnelMC.mc.setScreen(this.connectScreen);

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

	public void setCompressionMethod(PacketCompressionAlgorithm compressionAlgorithm) {
		this.bedrockClient.getSession().setCompression(compressionAlgorithm);
	}

	public void enableEncryption(SecretKey key) {
		if(this.bedrockClient.getSession().isEncrypted()) {
			throw new IllegalStateException("Connection is already encrypted");
		}
		this.bedrockClient.getSession().enableEncryption(key);
	}

	public boolean isConnected() {
		return this.bedrockClient.getRakNet() != null && this.bedrockClient.getRakNet().isRunning();
	}

	@SafeVarargs
	public final void expect(Class<? extends BedrockPacket>... packet) {
		this.expectedPackets.clear();
		this.expectedPackets.addAll(List.of(packet));
		this.expectedPackets.add(DisconnectPacket.class);
	}

	public List<Class<? extends BedrockPacket>> getExpectedPackets() {
		return Collections.unmodifiableList(this.expectedPackets);
	}

	public boolean isSpawned() {
		return this.spawned.get();
	}

	public Pair<SerializedSkin, Integer> getSerializedSkin(UUID uuid) {
		return this.serializedSkins.getOrDefault(uuid, null);
	}

	public void addSerializedSkin(UUID uuid, SerializedSkin skin) {
		if(!skin.getGeometryName().equals("geometry.humanoid.custom") && !skin.getGeometryName().equals("geometry.humanoid.customSlim")) {
			log.warn("Discarding unknown geometry skin: {}", skin.getGeometryName());
			return;
		}

		this.serializedSkins.compute(uuid, (uuid1, pair) -> {
			int version = 0;
			if(pair != null) {
				version = pair.second() + 1;
			}

			return Pair.of(skin, version);
		});
	}

	@Listener
	public void onEvent(SessionInitializedEvent event) {
		BedrockSession bedrockSession = event.getSession();
		FakeJavaConnection javaConnection = new FakeJavaConnection(this);

		bedrockSession.setPacketCodec(BedrockConnection.CODEC);
		bedrockSession.addDisconnectHandler(reason -> TunnelMC.mc.executeSync(() -> {
			// We disconnected ourselves.
			if (reason == DisconnectReason.DISCONNECTED) {
				return;
			}

			BedrockConnectionAccessor.closeConnection("You were disconnected from the target server because: " + reason.toString());
		}));

		bedrockSession.setBatchHandler(new ClientBatchHandler(this, javaConnection, this.packetTranslatorManager));
		bedrockSession.setLogging(false);

		try {
			this.expect(NetworkSettingsPacket.class);
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

	@Listener
	public void onEvent(PlayerSpawnedEvent event) {
		this.spawned.set(true);
	}

	@Listener
	public void onEvent(SessionClosedEvent event) {
		if(this.bedrockClient.getSession() != null) {
			this.bedrockClient.getSession().disconnect();
		}
		this.bedrockClient.close();
	}
}