package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.ClickSlotC2SPacketTranslator;
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
		if(!Client.instance.isConnectionOpen()) {
			return;
		}

		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;
		translator.onStackShiftClicked((ScreenHandler) (Object) this, slotIndex);
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"))
	private void placeSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!Client.instance.isConnectionOpen()) {
			return;
		}

		int count = button == ClickType.LEFT.ordinal() ? this.cursorStack.getCount() : 1;

		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;
		translator.onCursorStackClickEmptySlot((ScreenHandler) (Object) this, slotIndex, count);
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	private void takeSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!Client.instance.isConnectionOpen()) {
			return;
		}

		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;
		translator.onEmptyCursorClickStack((ScreenHandler) (Object) this, slotIndex);
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 3, target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"))
	private void addToSlotAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!Client.instance.isConnectionOpen()) {
			return;
		}

		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;
		translator.onCursorStackAddToStack((ScreenHandler) (Object) this, slotIndex);
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 3, target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;"))
	private void dropItemAction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!Client.instance.isConnectionOpen()) {
			return;
		}

		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;
		translator.onHoverOverStackDropItem((ScreenHandler) (Object) this, slotIndex, button);
	}
}
