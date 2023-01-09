package me.THEREALWWEFAN231.tunnelmc.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data.ClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(YggdrasilMinecraftSessionService.class)
public class MixinYggdrasilMinecraftSessionService {

    @Inject(method = "getTextures", at = @At("HEAD"), remap = false, cancellable = true)
    public void getTextures(GameProfile profile, boolean requireSecure, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cir) {
        if(!BedrockConnectionAccessor.isConnectionOpen()) {
            return;
        }

        BedrockConnection connection = BedrockConnectionAccessor.getCurrentConnection();
        Optional.ofNullable(connection.serializedSkins.get(profile.getId())).ifPresent(serializedSkin -> {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new HashMap<>();
            String armSize = serializedSkin.getArmSize();
            if(armSize.isEmpty()) {
                armSize = ClientData.ArmSizeType.WIDE.name();
            }

            map.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(profile.getId().toString(), getImageDataMetadata(serializedSkin.getSkinData(),
                    ClientData.ArmSizeType.valueOf(armSize.toUpperCase()))));
            if(serializedSkin.getCapeData().getImage().length != 0) {
                map.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(profile.getId().toString(), getImageDataMetadata(serializedSkin.getCapeData(),
                        ClientData.ArmSizeType.WIDE)));
            }

            cir.setReturnValue(map);
        });
    }

    private static Map<String, String> getImageDataMetadata(ImageData imageData, ClientData.ArmSizeType type) {
        Map<String, String> map = new HashMap<>();
        map.put("tunnelmc:width", String.valueOf(imageData.getWidth()));
        map.put("tunnelmc:height", String.valueOf(imageData.getHeight()));
        map.put("tunnelmc:data", Base64.getEncoder().encodeToString(imageData.getImage()));
        if(type == ClientData.ArmSizeType.SLIM) {
            map.put("model", "slim");
        }

        return map;
    }
}
