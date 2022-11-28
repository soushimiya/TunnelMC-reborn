package me.THEREALWWEFAN231.tunnelmc.mixins.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;

@Mixin(EntityPositionS2CPacket.class)
public interface IMixinEntityPositionS2CPacket {
	@Accessor("id")
	void setId(int newValue);

	@Accessor("x")
	void setX(double newValue);

	@Accessor("y")
	void setY(double newValue);

	@Accessor("z")
	void setZ(double newValue);

	@Accessor("yaw")
	void setYaw(byte newValue);

	@Accessor("pitch")
	void setPitch(byte newValue);

	@Accessor("onGround")
	void setOnGround(boolean newValue);
}
