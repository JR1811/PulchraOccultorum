package net.shirojr.pulchra_occultorum.item;

import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.shirojr.pulchra_occultorum.init.Entities;

public class UnicycleItem extends Item {
    public UnicycleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) return ActionResult.SUCCESS;
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        Entities.UNICYCLE.spawn(serverWorld, pos, SpawnReason.SPAWN_EGG);
        if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
