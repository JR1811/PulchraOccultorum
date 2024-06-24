package net.shirojr.pulchra_occultorum.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.shirojr.pulchra_occultorum.block.entity.MonolithBlockEntity;
import net.shirojr.pulchra_occultorum.entity.MonolithEntity;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.init.Entities;
import org.jetbrains.annotations.Nullable;

public class MonolithBlock extends BlockWithEntity {
    public static final MapCodec<MonolithBlock> CODEC = createCodec(MonolithBlock::new);

    public MonolithBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntities.MONOLITH_BLOCK_ENTITY, MonolithBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MonolithBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (!world.getBlockState(pos.up()).isAir()) return false;
        return super.canPlaceAt(state, world, pos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            BlockPos matchingPos = pos, entityPos = pos;
            BlockState matchingState = state;
            switch (state.get(Properties.DOUBLE_BLOCK_HALF)) {
                case UPPER -> {
                    matchingPos = matchingPos.down();
                    matchingState = state.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
                }
                case LOWER -> {
                    matchingPos = matchingPos.up();
                    entityPos = entityPos.up();
                    matchingState = state.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
                }
            }
            world.setBlockState(matchingPos, matchingState);
            MonolithEntity entity = new MonolithEntity(Entities.MONOLITH, world);
            entity.setPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            world.spawnEntity(entity);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockPos matchingPos = pos;
            switch (state.get(Properties.DOUBLE_BLOCK_HALF)) {
                case UPPER -> matchingPos = matchingPos.down();
                case LOWER -> matchingPos = matchingPos.up();
            }
            world.breakBlock(matchingPos, false);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
