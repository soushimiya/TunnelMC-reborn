package me.THEREALWWEFAN231.tunnelmc.mixins;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {
    @Shadow public abstract ItemPlacementContext getPlacementContext(ItemPlacementContext context);

    @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getPlacementContext(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/item/ItemPlacementContext;"))
    public ItemPlacementContext place(BlockItem instance, ItemPlacementContext context) {
        return this.getPlacementContext(context);

//         TODO: redo this garbage
//        System.out.println(context.getPlayerFacing());
//        if(context.getPlayerFacing().getAxis().isVertical()) {
//            return this.getPlacementContext(context);
//        }
//
//        BlockPos blockPos = context.getBlockPos();
//        World world = context.getWorld();
//        Direction direction = context.getSide() == Direction.UP ? context.getPlayerFacing() : Direction.UP;
////        if (context.shouldCancelInteraction()) {
////            direction = context.hitsInsideBlock() ? context.getSide().getOpposite() : context.getSide();
////        }
//
//        int i = 0;
//        BlockPos.Mutable mutable = blockPos.mutableCopy().move(direction);
//
//        while(i < 7) {
//            if (!world.isClient && !world.isInBuildLimit(mutable)) {
//                PlayerEntity playerEntity = context.getPlayer();
//                int j = world.getTopY();
//                if (playerEntity instanceof ServerPlayerEntity && mutable.getY() >= j) {
//                    ((ServerPlayerEntity)playerEntity).sendMessageToClient(Text.translatable("build.tooHigh", j - 1).formatted(Formatting.RED), true);
//                }
//                break;
//            }
//
//            BlockState blockState = world.getBlockState(mutable);
//            if (!blockState.isOf(instance.getBlock())) {
//                if (blockState.canReplace(context)) {
//                    context = ItemPlacementContext.offset(context, mutable, direction);
//                }
//                break;
//            }
//
//            mutable.move(direction);
//            if (direction.getAxis().isHorizontal()) {
//                ++i;
//            }
//        }
//        return context;
    }
}
