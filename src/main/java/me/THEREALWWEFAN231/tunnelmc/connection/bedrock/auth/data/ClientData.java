package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nukkitx.protocol.bedrock.data.skin.AnimatedTextureType;
import com.nukkitx.protocol.bedrock.data.skin.AnimationExpressionType;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.THEREALWWEFAN231.tunnelmc.utils.SkinUtils;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static me.THEREALWWEFAN231.tunnelmc.TunnelMC.JSON_MAPPER;

/**
 * ClientData is a container of client specific data of a Login packet. It holds data such as the skin of a
 * player, but also its language code and device information.
 * See <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/login/data.go">gophertunnel</a> for more (complete) information.
 */
@Data
public class ClientData {
    @JsonProperty("AnimatedImageData")
    private List<SkinAnimation> animatedImageData = new ArrayList<>();
    @JsonProperty("CapeData")
    private String capeData = "";
    @JsonProperty("CapeId")
    private String capeId = "";
    @JsonProperty("CapeImageHeight")
    private int capeImageHeight;
    @JsonProperty("CapeImageWidth")
    private int capeImageWidth;
    @JsonProperty("CapeOnClassicSkin")
    private boolean capeOnClassicSkin;
    @JsonProperty("ClientRandomId")
    private long clientRandomId;
    @JsonProperty("CurrentInputMode")
    private int currentInputMode; // Enum?
    @JsonProperty("DefaultInputMode")
    private int defaultInputMode; // Enum?
    @JsonProperty("DeviceModel")
    private String deviceModel = "";
    @JsonProperty("DeviceOS")
    private DeviceOS deviceOS;
    @JsonProperty("DeviceId")
    private String deviceId = "";
    @JsonProperty("GameVersion")
    private String gameVersion;
    @JsonProperty("GuiScale")
    private int guiScale;
    @JsonProperty("IsEditorMode")
    private boolean editorMode;
    @JsonProperty("LanguageCode")
    private String languageCode;
    @JsonProperty("PersonaSkin")
    private boolean personaSkin;
    @JsonProperty("PlatformOfflineId")
    private String platformOfflineId = "";
    @JsonProperty("PlatformOnlineId")
    private String platformOnlineId = "";
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty("PlatformUserId")
    private UUID platformUserId;
    @JsonProperty("PlayFabId")
    private String playFabId;
    @JsonProperty("PremiumSkin")
    private boolean premiumSkin;
    @JsonProperty("SelfSignedId")
    private UUID selfSignedId;
    @JsonProperty("ServerAddress")
    private String serverAddress;
    @JsonProperty("SkinAnimationData")
    private String skinAnimationData = "";
    @JsonProperty("SkinData")
    private String skinData;
    @JsonProperty("SkinGeometryData")
    private String skinGeometryData;
    @JsonProperty("SkinGeometryDataEngineVersion")
    private String skinGeometryVersion;
    @JsonProperty("SkinId")
    private String skinId;
    @JsonProperty("SkinImageHeight")
    private int skinImageHeight;
    @JsonProperty("SkinImageWidth")
    private int skinImageWidth;
    @JsonProperty("SkinResourcePatch")
    private String skinResourcePatch;
    @JsonProperty("SkinColor")
    private String skinColor = "#0";
    @JsonProperty("ArmSize")
    private ArmSizeType armSize;
    @JsonProperty("PersonaPieces")
    private List<PersonaPiece> personaPieces = new ArrayList<>();
    @JsonProperty("PieceTintColors")
    private List<PersonaPieceTintColor> personaPieceTintColors = new ArrayList<>();
    @JsonProperty("ThirdPartyName")
    private String thirdPartyName;
    @JsonProperty("ThirdPartyNameOnly")
    private boolean thirdPartyNameOnly;
    @JsonProperty("UIProfile")
    private int uiProfile;
    @JsonProperty("TrustedSkin")
    private boolean trustedSkin;

    public void setSkin(BufferedImage image) {
        ImageData imageData = SkinUtils.toImageData(image);
        this.setSkinData(Base64.getEncoder().encodeToString(imageData.getImage()));
        this.setSkinImageHeight(imageData.getHeight());
        this.setSkinImageWidth(imageData.getWidth());
        this.setSkinId(UUID.randomUUID() + "_Custom");
    }

    public void setCape(BufferedImage image) {
        ImageData imageData = SkinUtils.toImageData(image);
        this.setCapeData(Base64.getEncoder().encodeToString(imageData.getImage()));
        this.setCapeImageHeight(imageData.getHeight());
        this.setCapeImageWidth(imageData.getWidth());
        this.setCapeId(UUID.randomUUID().toString());
        this.setCapeOnClassicSkin(true);
    }

    public String getAsJWT(ChainData chainData) {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(chainData.publicKey().getEncoded());

        JsonNode headerNode = JSON_MAPPER.createObjectNode()
                .put("alg", "ES384")
                .put("x5u", publicKeyBase64);

        try {
            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(headerNode));
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(JSON_MAPPER.writeValueAsBytes(this));
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    chainData.signBytes(
                            (header + "." + payload).getBytes()));

            return header + "." + payload + "." + signature;
        } catch (JsonProcessingException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public record SkinAnimation(@JsonProperty("Frames") float frames,
                                @JsonProperty("Image") String image,
                                @JsonProperty("ImageHeight") int height,
                                @JsonProperty("ImageWidth") int width,
                                @JsonProperty("Type") AnimatedTextureType animatedTexture,
                                @JsonProperty("AnimationExpression") AnimationExpressionType animationExpression) {
    }

    public record PersonaPiece(@JsonProperty("IsDefault") boolean isDefault,
                               @JsonProperty("PackId") String packId,
                               @JsonProperty("PieceId") String pieceId,
                               @JsonProperty("PieceType") String pieceType,
                               @JsonProperty("ProductId") String productId) {
    }

    public record PersonaPieceTintColor(@JsonProperty("PieceType") boolean pieceType,
                                        @JsonProperty("Colors") List<String> colors) {
    }

    @Getter
    @RequiredArgsConstructor
    public enum ArmSizeType {
        @JsonProperty("wide") WIDE("geometry.humanoid.custom", "https://raw.githubusercontent.com/Flonja/TunnelMC/master/resources/steve.png"),
        @JsonProperty("slim") SLIM("geometry.humanoid.customSlim", "https://raw.githubusercontent.com/Flonja/TunnelMC/master/resources/alex.png");

        private final String geometryName;
        private final String defaultSkinUrl;

        public String getEncodedGeometryData() {
            return Base64.getEncoder().withoutPadding().encodeToString(("{\"geometry\":{\"default\":\"" + geometryName + "\"}}").getBytes(StandardCharsets.UTF_8));
        }

        public static ArmSizeType fromUUID(UUID uuid) {
            return (uuid.hashCode() & 1) == 1 ? SLIM : WIDE;
        }
    }
}
