package net.shirojr.pulchra_occultorum.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.shirojr.pulchra_occultorum.init.Tags;
import net.shirojr.pulchra_occultorum.util.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class FlagPoleBlock extends Block {
    private static final EnumProperty<BlockStateProperties.FlagPoleState> POLE_STATE = BlockStateProperties.FLAG_POLE_STATE;

    public FlagPoleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POLE_STATE, BlockStateProperties.FlagPoleState.MIDDLE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POLE_STATE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POLE_STATE, BlockStateProperties.FlagPoleState.TOP);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.getItem().equals(net.shirojr.pulchra_occultorum.init.Blocks.FLAG_POLE.asItem()))
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);

        BlockPos posForPlacement = pos;
        do {
            world.updateNeighbors(posForPlacement, world.getBlockState(posForPlacement).getBlock());
            posForPlacement = posForPlacement.up();
        }
        while (world.getBlockState(posForPlacement).getBlock() instanceof FlagPoleBlock);

        if (!world.getBlockState(posForPlacement).isAir()) {
            // TODO: check for more than just air block (canReplace() ?)
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        if (!player.isCreative()) stack.decrement(1);
        modifyFlagPoleStates(world, posForPlacement);
        world.setBlockState(posForPlacement, this.getDefaultState().with(POLE_STATE, BlockStateProperties.FlagPoleState.TOP), NOTIFY_ALL_AND_REDRAW);
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isSupported(world, pos);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!isSupported(world, pos)) return Blocks.AIR.getDefaultState();
        BlockStateProperties.FlagPoleState poleState = BlockStateProperties.FlagPoleState.MIDDLE;
        if (world.getBlockState(pos.up()).isAir()) poleState = BlockStateProperties.FlagPoleState.TOP;
        return this.getDefaultState().with(POLE_STATE, poleState);
    }

    private void modifyFlagPoleStates(WorldAccess world, BlockPos currentPos) {
        BlockState state = world.getBlockState(currentPos);
        BlockState stateFromAbove = world.getBlockState(currentPos.up());

        if (!(state.getBlock() instanceof FlagPoleBlock)) return;
        if (stateFromAbove.getBlock() instanceof FlagPoleBlock && state.get(POLE_STATE).equals(BlockStateProperties.FlagPoleState.TOP)) {
            BlockState cleanedState = state.with(POLE_STATE, BlockStateProperties.FlagPoleState.TOP);
            world.setBlockState(currentPos, cleanedState, NOTIFY_ALL_AND_REDRAW);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockState stateBelow = world.getBlockState(pos.down());
        if (!world.isClient() && stateBelow.getBlock() instanceof FlagPoleBlock) {
            world.setBlockState(pos.down(), stateBelow.with(POLE_STATE, BlockStateProperties.FlagPoleState.MIDDLE), NOTIFY_ALL_AND_REDRAW);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    private boolean isSupported(WorldView world, BlockPos pos) {
        boolean supportsFlagPoleBelow = world.getBlockState(pos.down()).isIn(Tags.Blocks.SUPPORTS_FLAG_POLE);
        return supportsFlagPoleBelow && !world.isOutOfHeightLimit(pos);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
        if (state.get(POLE_STATE).equals(BlockStateProperties.FlagPoleState.TOP)) {
            shape = VoxelShapes.union(shape, Block.createCuboidShape(5, 14, 5, 11, 16, 11));
        }
        return shape;
    }
}
