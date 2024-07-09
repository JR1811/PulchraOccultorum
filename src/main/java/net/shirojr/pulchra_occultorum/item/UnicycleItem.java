package net.shirojr.pulchra_occultorum.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.init.Entities;

public class UnicycleItem extends Item {
    public UnicycleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient()) return ActionResult.SUCCESS;
        UnicycleEntity entity = new UnicycleEntity(Entities.UNICYCLE, context.getWorld());
        BlockPos pos = context.getBlockPos().offset(context.getSide()).up();
        pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        context.getWorld().spawnEntity(entity);
        if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
