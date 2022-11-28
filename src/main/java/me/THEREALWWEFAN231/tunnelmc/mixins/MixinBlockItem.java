package me.THEREALWWEFAN231.tunnelmc.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {
    @Shadow public abstract Block getBlock();

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "HEAD"))
    public void place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.canPlace()) {
            return;
        }
        if(context.getPlayerFacing().getAxis().isVertical()) {
            return;
        }

        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        Direction direction;
        if (context.shouldCancelInteraction()) {
            direction = context.hitsInsideBlock() ? context.getSide().getOpposite() : context.getSide();
        } else {
            direction = context.getPlayerFacing();
        }

        int i = 0;
        BlockPos.Mutable mutable = blockPos.mutableCopy().move(direction);

        while(i < 7) {
            if (!world.isClient && !world.isInBuildLimit(mutable)) {
                PlayerEntity playerEntity = context.getPlayer();
                int j = world.getTopY();
                if (playerEntity instanceof ServerPlayerEntity && mutable.getY() >= j) {
                    ((ServerPlayerEntity)playerEntity).sendMessageToClient(Text.translatable("build.tooHigh", new Object[]{j - 1}).formatted(Formatting.RED), true);
                }
                break;
            }

            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isOf(this.getBlock())) {
                if (blockState.canReplace(context)) {
                    context = ItemPlacementContext.offset(context, mutable, direction);
                }
                break;
            }

            mutable.move(direction);
            if (direction.getAxis().isHorizontal()) {
                ++i;
            }
        }
    }
}
