package net.shirojr.pulchra_occultorum.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.pulchra_occultorum.init.ScreenHandlers;
import org.jetbrains.annotations.Nullable;

public class SpotlightLampScreenHandler extends ScreenHandler {
    private final Inventory playerInventory;

    public SpotlightLampScreenHandler(int syncId, Inventory playerInventory) {
        super(ScreenHandlers.SPOTLIGHT_LAMP_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}
