package me.THEREALWWEFAN231.tunnelmc.mixins;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.THEREALWWEFAN231.tunnelmc.bedrockconnection.Client;
import me.THEREALWWEFAN231.tunnelmc.javaconnection.packet.ClickSlotC2SPacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {

	//I know I shouldn't use @Overwrite but for now/testing purposes I will use it!!!!! It's not really a big deal though also I used engima to get this code rather then the source fabric attaches(because of the wack while loops)
	@Inject(method = "internalOnSlotClick", at =  @At("RETURN"))
	private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if(!Client.instance.isConnectionOpen()) {//if the connection isn't open, do the normal click stuff
			return;
		}
		
		ClickSlotC2SPacketTranslator translator = Client.instance.javaConnection.packetTranslatorManager.clickSlotTranslator;

		ItemStack itemStack6 = ItemStack.EMPTY;
		ScreenHandler screenHandler = player.currentScreenHandler;
		PlayerInventory inventory = player.getInventory();
		if (actionType == SlotActionType.QUICK_CRAFT) {
			int integer8 = this.quickCraftButton;
			this.quickCraftButton = ScreenHandler.unpackQuickCraftStage(button);
			if ((integer8 != 1 || this.quickCraftButton != 2) && integer8 != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (screenHandler.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftButton == 0) {
				this.quickCraftStage = ScreenHandler.unpackQuickCraftButton(button);
				if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftStage, player)) {
					this.quickCraftButton = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftButton == 1) {
				Slot slot9 = this.slots.get(slotIndex);
				ItemStack itemStack10 = screenHandler.getCursorStack();
				if (slot9 != null && ScreenHandler.canInsertItemIntoSlot(slot9, itemStack10, true) && slot9.canInsert(itemStack10) && (this.quickCraftStage == 2 || itemStack10.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot9)) {
					this.quickCraftSlots.add(slot9);
				}
			} else if (this.quickCraftButton == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					ItemStack itemStack9 = screenHandler.getCursorStack().copy();
					int integer10 = screenHandler.getCursorStack().getCount();
					for (final Slot lv6 : this.quickCraftSlots) {
						ItemStack itemStack13 = screenHandler.getCursorStack();
						if (lv6 != null && ScreenHandler.canInsertItemIntoSlot(lv6, itemStack13, true) && lv6.canInsert(itemStack13) && (this.quickCraftStage == 2 || itemStack13.getCount() >= this.quickCraftSlots.size()) && this.canInsertIntoSlot(lv6)) {
							ItemStack itemStack14 = itemStack9.copy();
							int integer15 = lv6.hasStack() ? lv6.getStack().getCount() : 0;
							ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack14, integer15);
							int integer16 = Math.min(itemStack14.getMaxCount(), lv6.getMaxItemCount(itemStack14));
							if (itemStack14.getCount() > integer16) {
								itemStack14.setCount(integer16);
							}
							integer10 -= itemStack14.getCount() - integer15;
							lv6.setStack(itemStack14);
						}
					}
					itemStack9.setCount(integer10);
					screenHandler.setCursorStack(itemStack9);
				}
				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftButton != 0) {
			this.endQuickCraft();
		} else if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1)) {
			if (slotIndex == -999) {
				if (!screenHandler.getCursorStack().isEmpty()) {
					if (button == 0) {
						player.dropItem(screenHandler.getCursorStack(), true);
						screenHandler.setCursorStack(ItemStack.EMPTY);
					}
					if (button == 1) {
						player.dropItem(screenHandler.getCursorStack().split(1), true);
					}
				}
			} else if (actionType == SlotActionType.QUICK_MOVE) {
				if (slotIndex < 0) {
					callbackInfoReturnable.setReturnValue(ItemStack.EMPTY);
					return;
				}
				Slot slot8 = this.slots.get(slotIndex);
				if (slot8 == null || !slot8.canTakeItems(player)) {
					callbackInfoReturnable.setReturnValue(ItemStack.EMPTY);
					return;
				}
				for (ItemStack lv10 = this.transferSlot(player, slotIndex); !lv10.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot8.getStack(), lv10); lv10 = this.transferSlot(player, slotIndex)) {
					itemStack6 = lv10.copy();
				}
				
				//EDITED
				translator.onStackShiftClicked((ScreenHandler) (Object) this, slotIndex);
			} else {//PICKUP 
				if (slotIndex < 0) {
					callbackInfoReturnable.setReturnValue(ItemStack.EMPTY);
					return;
				}
				Slot clickedSlot = this.slots.get(slotIndex);
				if (clickedSlot != null) {
					ItemStack clickedSlotStack = clickedSlot.getStack();
					ItemStack cursorStack = screenHandler.getCursorStack();
					if (!clickedSlotStack.isEmpty()) {
						itemStack6 = clickedSlotStack.copy();
					}
					if (clickedSlotStack.isEmpty()) {
						if (!cursorStack.isEmpty() && clickedSlot.canInsert(cursorStack)) {
							int itemCountToMoveFromCursorToClickedSlot = (button == 0) ? cursorStack.getCount() : 1;
							if (itemCountToMoveFromCursorToClickedSlot > clickedSlot.getMaxItemCount(cursorStack)) {
								itemCountToMoveFromCursorToClickedSlot = clickedSlot.getMaxItemCount(cursorStack);
							}
							clickedSlot.setStack(cursorStack.split(itemCountToMoveFromCursorToClickedSlot));
							//EDITED
							translator.onCursorStackClickEmptySlot((ScreenHandler) (Object) this, slotIndex, itemCountToMoveFromCursorToClickedSlot);
						}
					} else if (clickedSlot.canTakeItems(player)) {
						if (cursorStack.isEmpty()) {
							if (clickedSlotStack.isEmpty()) {
								//this isn't ever done?!?!
								clickedSlot.setStack(ItemStack.EMPTY);
								screenHandler.setCursorStack(ItemStack.EMPTY);
							} else {
								int integer11 = (button == 0) ? clickedSlotStack.getCount() : ((clickedSlotStack.getCount() + 1) / 2);
								screenHandler.setCursorStack(clickedSlot.takeStack(integer11));
								if (clickedSlotStack.isEmpty()) {
									clickedSlot.setStack(ItemStack.EMPTY);
								}
								clickedSlot.onTakeItem(player, screenHandler.getCursorStack());

								//EDITED
								translator.onEmptyCursorClickStack((ScreenHandler) (Object) this, slotIndex);
							}
						} else if (clickedSlot.canInsert(cursorStack)) {
							if (ItemStack.canCombine(clickedSlotStack, cursorStack)) {
								int integer11 = (button == 0) ? cursorStack.getCount() : 1;
								if (integer11 > clickedSlot.getMaxItemCount(cursorStack) - clickedSlotStack.getCount()) {
									integer11 = clickedSlot.getMaxItemCount(cursorStack) - clickedSlotStack.getCount();
								}
								if (integer11 > cursorStack.getMaxCount() - clickedSlotStack.getCount()) {
									integer11 = cursorStack.getMaxCount() - clickedSlotStack.getCount();
								}
								cursorStack.decrement(integer11);
								clickedSlotStack.increment(integer11);
								
								//EDITED
								translator.onCursorStackAddToStack((ScreenHandler) (Object) this, slotIndex);
								
							} else if (cursorStack.getCount() <= clickedSlot.getMaxItemCount(cursorStack)) {
								clickedSlot.setStack(cursorStack);
								screenHandler.setCursorStack(clickedSlotStack);
							}
						} else if (cursorStack.getMaxCount() > 1 && ItemStack.canCombine(clickedSlotStack, cursorStack) && !clickedSlotStack.isEmpty()) {
							int integer11 = clickedSlotStack.getCount();
							if (integer11 + cursorStack.getCount() <= cursorStack.getMaxCount()) {
								cursorStack.increment(integer11);
								clickedSlotStack = clickedSlot.takeStack(integer11);
								if (clickedSlotStack.isEmpty()) {
									clickedSlot.setStack(ItemStack.EMPTY);
								}
								clickedSlot.onTakeItem(player, screenHandler.getCursorStack());
							}
						}
					}
					clickedSlot.markDirty();
				}
			}
		} else if (actionType == SlotActionType.SWAP) {
			Slot slot8 = this.slots.get(slotIndex);
			ItemStack itemStack9 = inventory.getStack(button);
			ItemStack itemStack10 = slot8.getStack();
			if (!itemStack9.isEmpty() || !itemStack10.isEmpty()) {
				if (itemStack9.isEmpty()) {
					if (slot8.canTakeItems(player)) {
						inventory.setStack(button, itemStack10);
						((IMixinSlot) slot8).invokeOnTake(itemStack10.getCount());
						slot8.setStack(ItemStack.EMPTY);
						slot8.onTakeItem(player, itemStack10);
					}
				} else if (itemStack10.isEmpty()) {
					if (slot8.canInsert(itemStack9)) {
						int integer11 = slot8.getMaxItemCount(itemStack9);
						if (itemStack9.getCount() > integer11) {
							slot8.setStack(itemStack9.split(integer11));
						} else {
							slot8.setStack(itemStack9);
							inventory.setStack(button, ItemStack.EMPTY);
						}
					}
				} else if (slot8.canTakeItems(player) && slot8.canInsert(itemStack9)) {
					int integer11 = slot8.getMaxItemCount(itemStack9);
					if (itemStack9.getCount() > integer11) {
						slot8.setStack(itemStack9.split(integer11));
						slot8.onTakeItem(player, itemStack10);
						if (!inventory.insertStack(itemStack10)) {
							player.dropItem(itemStack10, true);
						}
					} else {
						slot8.setStack(itemStack9);
						inventory.setStack(button, itemStack10);
						slot8.onTakeItem(player, itemStack10);
					}
				}
			}
		} else if (actionType == SlotActionType.CLONE && player.getAbilities().creativeMode && screenHandler.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot8 = this.slots.get(slotIndex);
			if (slot8 != null && slot8.hasStack()) {
				ItemStack itemStack9 = slot8.getStack().copy();
				itemStack9.setCount(itemStack9.getMaxCount());
				screenHandler.setCursorStack(itemStack9);
			}
		} else if (actionType == SlotActionType.THROW && screenHandler.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot8 = this.slots.get(slotIndex);
			if (slot8 != null && slot8.hasStack() && slot8.canTakeItems(player)) {
				ItemStack itemStack9 = slot8.takeStack((button == 0) ? 1 : slot8.getStack().getCount());
				slot8.onTakeItem(player, itemStack9);
				player.dropItem(itemStack9, true);

				//EDITED
				translator.onHoverOverStackDropItem((ScreenHandler) (Object) this, slotIndex, button);
			}
		} else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
			Slot slot8 = this.slots.get(slotIndex);
			ItemStack itemStack9 = screenHandler.getCursorStack();
			if (!itemStack9.isEmpty() && (slot8 == null || !slot8.hasStack() || !slot8.canTakeItems(player))) {
				int integer10 = (button == 0) ? 0 : (this.slots.size() - 1);
				int integer11 = (button == 0) ? 1 : -1;
				for (int w = 0; w < 2; ++w) {
					for (int x = integer10; x >= 0 && x < this.slots.size() && itemStack9.getCount() < itemStack9.getMaxCount(); x += integer11) {
						Slot slot14 = this.slots.get(x);
						if (slot14.hasStack() && ScreenHandler.canInsertItemIntoSlot(slot14, itemStack9, true) && slot14.canTakeItems(player) && this.canInsertIntoSlot(itemStack9, slot14)) {
							ItemStack itemStack15 = slot14.getStack();
							if (w != 0 || itemStack15.getCount() != itemStack15.getMaxCount()) {
								int integer16 = Math.min(itemStack9.getMaxCount() - itemStack9.getCount(), itemStack15.getCount());
								ItemStack itemStack17 = slot14.takeStack(integer16);
								itemStack9.increment(integer16);
								if (itemStack17.isEmpty()) {
									slot14.setStack(ItemStack.EMPTY);
								}
								slot14.onTakeItem(player, itemStack17);
							}
						}
					}
				}
			}
			this.sendContentUpdates();
		}
		
		ci.setReturnValue(itemStack6);
	}

	@Shadow
	private int quickCraftButton;
	@Shadow
	private int quickCraftStage;
	@Shadow
	@Final
	private Set<Slot> quickCraftSlots;
	@Shadow
	@Final
	public List<Slot> slots;

	@Shadow
	protected void endQuickCraft() {

	}

	@Shadow
	public void sendContentUpdates() {

	}

	@Shadow
	public ItemStack transferSlot(PlayerEntity player, int index) {
		return null;
	}

	@Shadow
	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	@Shadow
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

}
