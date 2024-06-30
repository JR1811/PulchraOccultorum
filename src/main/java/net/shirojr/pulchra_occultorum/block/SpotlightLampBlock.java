package net.shirojr.pulchra_occultorum.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import org.jetbrains.annotations.Nullable;

public class SpotlightLampBlock extends BlockWithEntity {
    public static final MapCodec<SpotlightLampBlock> CODEC = createCodec(SpotlightLampBlock::new);
    public static final BooleanProperty LIT = Properties.LIT;

    public SpotlightLampBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(LIT, false);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getBlockState(pos.down()).isAir()) return false;
        return super.canPlaceAt(state, world, pos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpotlightLampBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntities.SPOTLIGHT_LAMP_BLOCK_ENTITY, SpotlightLampBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        boolean isLit = state.get(LIT);
        SoundEvent soundEvent = SoundEvents.BLOCK_LEVER_CLICK;
        if (!world.isClient() && world.getReceivedRedstonePower(hit.getBlockPos()) > 0) {
            state = state.with(LIT, !isLit);
            world.setBlockState(pos, state);
            world.playSound(null, hit.getBlockPos(), soundEvent, SoundCategory.BLOCKS, 1.0f, 0.5f);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state,world, pos, player, hit);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world.getReceivedRedstonePower(pos.down()) > 0) {
            world.setBlockState(pos, state.with(LIT, true), NOTIFY_ALL_AND_REDRAW);
            //TODO: change strength field in BE
        } else {
            world.setBlockState(pos, state.with(LIT, false), NOTIFY_ALL_AND_REDRAW);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(6 , 0, 6, 10, 12, 10);

    }

    public static int luminanceFromBlockState(BlockState state) {
        if (!state.contains(LIT)) return 0;
        return state.get(LIT) ? 10 : 0;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
