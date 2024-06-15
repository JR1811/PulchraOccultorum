package net.shirojr.pulchra_occultorum.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.CustomDataComponents;
import net.shirojr.pulchra_occultorum.util.handler.ItemTickHandler;

public abstract class AbstractTickingItem extends Item implements ItemTickHandler {
    public AbstractTickingItem(Settings settings) {
        super(settings.component(CustomDataComponents.TICK, 0));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        int tick = getTick(stack);
        incrementTick(stack);
        if (hasMaxTick(stack) && tick >= getMaxTick(stack)) {
            resetAndStopTicking(stack);
        }
    }

    @Override
    public int getTick(ItemStack stack) {
        return stack.getOrDefault(CustomDataComponents.TICK, 0);
    }

    @Override
    public void setTick(ItemStack stack, int tick) {
        if (hasMaxTick(stack)) tick = Math.min(getMaxTick(stack), tick);
        stack.set(CustomDataComponents.TICK, tick);
    }

    @Override
    public void incrementTick(ItemStack stack) {
        int tick = getTick(stack);
        if (tick < 1) return;
        tick ++;
        stack.set(CustomDataComponents.TICK, tick);
    }

    @Override
    public boolean hasMaxTick(ItemStack stack) {
        return getMaxTick(stack) > 0;
    }

    /**
     * Can be used to reset the tick automatically to the initial value
     *
     * @return if set to 0, it will count up infinitely
     */
    @Override
    public int getMaxTick(ItemStack stack) {
        return 0;
    }

    @Override
    public void startTicking(ItemStack stack) {
        if (getTick(stack) > 0) return;
        setTick(stack, 1);
    }

    @Override
    public void resetAndStopTicking(ItemStack stack) {
        stack.set(CustomDataComponents.TICK, 0);
    }

    @Override
    public boolean isTicking(ItemStack stack) {
        return getTick(stack) > 0;
    }

    @Override
    public int getTicksLeft(ItemStack stack) {
        return Math.max(0, getMaxTick(stack) - getTick(stack));
    }
}
