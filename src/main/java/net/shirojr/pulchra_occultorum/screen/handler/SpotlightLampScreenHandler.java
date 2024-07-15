package net.shirojr.pulchra_occultorum.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.init.ScreenHandlers;

public class SpotlightLampScreenHandler extends ScreenHandler {
    @SuppressWarnings("FieldCanBeLocal")
    private final PlayerInventory playerInventory;
    private SpotlightLampBlockEntity blockEntity;

    public SpotlightLampScreenHandler(int syncId, PlayerInventory playerInventory, SpotlightLampBlockEntity.Data data) {
        super(ScreenHandlers.SPOTLIGHT_LAMP_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
        if (playerInventory.player.getWorld() instanceof World world) {
            if (world.getBlockEntity(data.pos()) instanceof SpotlightLampBlockEntity spotlightLampBlockEntity) {
                this.blockEntity = spotlightLampBlockEntity;
            }
        }
    }

    public SpotlightLampBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
