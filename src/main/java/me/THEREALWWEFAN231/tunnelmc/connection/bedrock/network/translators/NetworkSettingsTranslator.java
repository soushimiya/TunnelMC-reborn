package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import io.netty.util.AsciiString;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ClientData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.DeviceOS;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.utils.FileUtils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

@PacketIdentifier(NetworkSettingsPacket.class)
public class NetworkSettingsTranslator extends PacketTranslator<NetworkSettingsPacket> {

    @Override
    public void translate(NetworkSettingsPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        bedrockConnection.setCompressionMethod(packet.getCompressionAlgorithm());

        LoginPacket loginPacket = new LoginPacket();

        UUID uuid = Objects.requireNonNull(TunnelMC.mc.getSession().getUuidOrNull());
        ClientData clientData = new ClientData();
        clientData.setArmSize(ClientData.ArmSizeType.fromUUID(uuid));
        clientData.setClientRandomId(uuid.getLeastSignificantBits());
        clientData.setCurrentInputMode(1);
        clientData.setDefaultInputMode(1);
        clientData.setPlayFabId("");
        clientData.setDeviceOS(DeviceOS.MICROSOFT_WINDOWS_10);
        clientData.setGameVersion(BedrockConnection.CODEC.getMinecraftVersion());
        clientData.setSkinGeometryVersion(Base64.getEncoder().withoutPadding().encodeToString(BedrockConnection.CODEC.getMinecraftVersion().getBytes(StandardCharsets.UTF_8)));
        clientData.setLanguageCode(TunnelMC.mc.getLanguageManager().getLanguage().getCode());
        clientData.setSelfSignedId(uuid);
        clientData.setServerAddress(bedrockConnection.getTargetAddress().getHostName() + ":" + bedrockConnection.getTargetAddress().getPort());
        clientData.setThirdPartyName(bedrockConnection.getAuthData().displayName());
        clientData.setSkinResourcePatch(clientData.getArmSize().getEncodedGeometryData());
        clientData.setTrustedSkin(true);

        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = TunnelMC.mc.getSessionService().getTextures(TunnelMC.mc.getSession().getProfile(), false);
        try {
            MinecraftProfileTexture skinTexture = Optional.ofNullable(textures.get(MinecraftProfileTexture.Type.SKIN))
                    .orElse(new MinecraftProfileTexture(clientData.getArmSize().getDefaultSkinUrl(), Collections.emptyMap()));
            clientData.setSkin(ImageIO.read(new URL(skinTexture.getUrl())));

            clientData.setSkinGeometryData(Base64.getEncoder().withoutPadding().encodeToString(
                    JSON_MAPPER.writeValueAsBytes(FileUtils.getJsonFromResource("tunnel/geometry_data.json"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Optional.ofNullable(textures.get(MinecraftProfileTexture.Type.CAPE))
                .ifPresent(capeTexture -> {
                    try {
                        clientData.setCape(ImageIO.read(new URL(capeTexture.getUrl())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        bedrockConnection.expect(ServerToClientHandshakePacket.class, PlayStatusPacket.class);

        loginPacket.setProtocolVersion(BedrockConnection.CODEC.getProtocolVersion());
        loginPacket.setChainData(new AsciiString(bedrockConnection.getChainData().rawData().getBytes()));
        loginPacket.setSkinData(new AsciiString(clientData.getAsJWT(bedrockConnection.getChainData())));
        bedrockConnection.sendPacketImmediately(loginPacket);
    }
}
