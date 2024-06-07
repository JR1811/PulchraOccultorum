package net.shirojr.pulchra_occultorum.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.PulchraOccultorumBlockEntities;
import net.shirojr.pulchra_occultorum.util.AbstractTickingBlockEntity;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class TestBlockEntity extends AbstractTickingBlockEntity {

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(PulchraOccultorumBlockEntities.TEST_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TestBlockEntity blockEntity) {
        if (world.isClient()) return;
        blockEntity.incrementTick();
        if (blockEntity.getTick() >= 100) blockEntity.resetTick();
        LoggerUtil.devLogger("Ticked BE: " + blockEntity.getTick());
    }
}
