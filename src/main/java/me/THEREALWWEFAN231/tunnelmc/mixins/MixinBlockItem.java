package me.THEREALWWEFAN231.tunnelmc.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {
    // TODO: this is shit

    @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getPlacementContext(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/item/ItemPlacementContext;"))
    public ItemPlacementContext place(BlockItem instance, ItemPlacementContext context) {
        System.out.println(context);
        if(context.getPlayerFacing().getAxis().isVertical()) {
            return null;
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
                    ((ServerPlayerEntity)playerEntity).sendMessageToClient(Text.translatable("build.tooHigh", j - 1).formatted(Formatting.RED), true);
                }
                break;
            }

            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isOf(instance.getBlock())) {
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
        return context;
    }
}
