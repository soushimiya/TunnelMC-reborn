package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnectionAccessor;
import me.THEREALWWEFAN231.tunnelmc.events.slot.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler {
	@Shadow private ItemStack cursorStack;

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/screen/ScreenHandler;transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"))
	private void quickMoveAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		TunnelMC.getInstance().getEventManager().fire(new QuickMoveSlotEvent((ScreenHandler) (Object) this, slotIndex));
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"))
	private void placeSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		int count = button == ClickType.LEFT.ordinal() ? this.cursorStack.getCount() : 1;
		TunnelMC.getInstance().getEventManager().fire(new PlaceStackOnEmptySlotEvent((ScreenHandler) (Object) this, slotIndex, count));
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	private void takeSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		TunnelMC.getInstance().getEventManager().fire(new TakeSlotEvent((ScreenHandler) (Object) this, slotIndex));
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 3, target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"))
	private void addToSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		int count = button == ClickType.LEFT.ordinal() ? this.cursorStack.getCount() : 1;
		TunnelMC.getInstance().getEventManager().fire(new PlaceStackSlotEvent((ScreenHandler) (Object) this, slotIndex, count));
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 3, target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;"))
	private void dropItemAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!BedrockConnectionAccessor.isConnectionOpen()) {
			return;
		}

		TunnelMC.getInstance().getEventManager().fire(new DropSlotEvent((ScreenHandler) (Object) this, slotIndex, button));
	}
}
