package net.shirojr.pulchra_occultorum.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingBlockEntity;

public class MonolithBlockEntity extends AbstractTickingBlockEntity {
    public MonolithBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MONOLITH_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MonolithBlockEntity blockEntity) {

    }

}
