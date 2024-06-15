package net.shirojr.pulchra_occultorum.util.handler;

import net.minecraft.item.ItemStack;

public interface ItemTickHandler {
    int getTick(ItemStack stack);
    void setTick(ItemStack stack, int tick);
    void incrementTick(ItemStack stack);
    void resetAndStopTicking(ItemStack stack);
    boolean hasMaxTick(ItemStack stack);
    int getMaxTick(ItemStack stack);

    void startTicking(ItemStack stack);
    boolean isTicking(ItemStack stack);
    int getTicksLeft(ItemStack stack);
}
