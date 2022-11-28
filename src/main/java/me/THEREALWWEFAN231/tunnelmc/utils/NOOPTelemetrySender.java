package me.THEREALWWEFAN231.tunnelmc.utils;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.telemetry.TelemetrySender;

import java.util.Optional;
import java.util.UUID;

public class NOOPTelemetrySender extends TelemetrySender {
    public static final TelemetrySender INSTANCE = new NOOPTelemetrySender();

    public NOOPTelemetrySender() {
        super(MinecraftClient.getInstance(), UserApiService.OFFLINE, Optional.empty(), Optional.empty(), new UUID(0, 0));
    }
}
