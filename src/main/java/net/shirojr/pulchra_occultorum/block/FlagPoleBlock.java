package net.shirojr.pulchra_occultorum.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.shirojr.pulchra_occultorum.block.entity.FlagPoleBlockEntity;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.init.Tags;
import net.shirojr.pulchra_occultorum.util.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlagPoleBlock extends BlockWithEntity {
    public static final MapCodec<FlagPoleBlock> CODEC = createCodec(FlagPoleBlock::new);
    private static final EnumProperty<BlockStateProperties.FlagPoleState> POLE_STATE = BlockStateProperties.FLAG_POLE_STATE;

    public FlagPoleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(POLE_STATE, BlockStateProperties.FlagPoleState.MIDDLE)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FlagPoleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntities.FLAG_POLE_BLOCK_ENTITY, FlagPoleBlockEntity::tick);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POLE_STATE, Properties.HORIZONTAL_FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POLE_STATE, BlockStateProperties.FlagPoleState.TOP).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
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
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        if (!player.isCreative()) stack.decrement(1);

        BlockState finalState = world.getBlockState(pos).with(POLE_STATE, BlockStateProperties.FlagPoleState.TOP);
        if (world.getBlockState(posForPlacement).contains(Properties.HORIZONTAL_FACING)) {
            finalState = finalState.with(Properties.HORIZONTAL_FACING, world.getBlockState(posForPlacement.down()).get(Properties.HORIZONTAL_FACING));
        }
        world.setBlockState(posForPlacement, finalState, NOTIFY_ALL_AND_REDRAW);
        world.playSound(null, posForPlacement, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.7f, 0.7f);

        return ItemActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(hit.getBlockPos().up()) instanceof FlagPoleBlockEntity blockEntity && !blockEntity.getFlagInventory().isEmpty()) {
            if (blockEntity.getFlagPos() != null && !world.isClient()) {
                world.playSound(null, blockEntity.getFlagPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.7f, 0.7f);
                if (blockEntity.dropFlagInventory(player)) {
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isSupported(world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!isSupported(world, pos)) {
            boolean shouldDrop = false;
            var flagPoleState = state.getOrEmpty(BlockStateProperties.FLAG_POLE_STATE);
            if (flagPoleState.isPresent() && flagPoleState.get().equals(BlockStateProperties.FlagPoleState.TOP)) {
                shouldDrop = true;
            }
            world.breakBlock(pos, shouldDrop);
            return state;
        }
        var poleType = BlockStateProperties.FlagPoleState.MIDDLE;
        if (!world.getBlockState(pos.up()).getBlock().equals(state.getBlock()))
            poleType = BlockStateProperties.FlagPoleState.TOP;
        return state.with(POLE_STATE, poleType);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockState stateBelow = world.getBlockState(pos.down());
        if (stateBelow.getBlock() instanceof FlagPoleBlock) {
            world.setBlockState(pos.down(), stateBelow.with(POLE_STATE, BlockStateProperties.FlagPoleState.MIDDLE), NOTIFY_ALL_AND_REDRAW);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock()) || newState.contains(BlockStateProperties.FLAG_POLE_STATE)) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }
        if (world.getBlockEntity(pos) instanceof FlagPoleBlockEntity blockEntity) {
            if (!world.isClient() && !blockEntity.getFlagInventory().isEmpty()) {
                ItemScatterer.spawn(world, pos, blockEntity.getFlagInventory());
                // blockEntity.syncedInventoryModification(inventory -> { ... });
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private boolean isSupported(WorldView world, BlockPos pos) {
        boolean supportsFlagPoleBelow = world.getBlockState(pos.down()).isIn(Tags.Blocks.SUPPORTS_FLAG_POLE);
        return supportsFlagPoleBelow && !world.isOutOfHeightLimit(pos);
    }

    @Nullable
    public static BlockPos getBaseBlockPos(World world, BlockPos flagPolePos) {
        BlockPos flagPolePosWalker = flagPolePos;
        if (!world.getBlockState(flagPolePos).contains(BlockStateProperties.FLAG_POLE_STATE)) return null;
        while (world.getBlockState(flagPolePosWalker).contains(BlockStateProperties.FLAG_POLE_STATE)) {
            flagPolePosWalker = flagPolePosWalker.down();
        }
        return flagPolePosWalker;
    }

    @Nullable
    public static BlockPos getTopBlockPos(WorldAccess world, BlockPos flagPolePos) {
        BlockPos flagPolePosWalker = flagPolePos;
        if (!world.getBlockState(flagPolePos).contains(BlockStateProperties.FLAG_POLE_STATE)) return null;
        while (world.getBlockState(flagPolePosWalker).contains(BlockStateProperties.FLAG_POLE_STATE)) {
            flagPolePosWalker = flagPolePosWalker.up();
        }
        return flagPolePosWalker.down();
    }

    public static int getFlagPoleBlockCount(World world, BlockPos pos) {
        BlockPos posWalker = pos;
        int count = 0;
        while (world.getBlockState(posWalker).contains(BlockStateProperties.FLAG_POLE_STATE)) {
            posWalker = posWalker.down();
            count++;
        }
        posWalker = pos.up();
        while (world.getBlockState(posWalker).contains(BlockStateProperties.FLAG_POLE_STATE)) {
            posWalker = posWalker.up();
            count++;
        }
        return count;
    }

    /**
     * The first pos in the list will always be the base block and not a pos block!
     */
    public static List<BlockPos> connectedFlagPoleBlocks(World world, BlockPos pos) {
        BlockPos bottom = getBaseBlockPos(world, pos);
        BlockPos top = getTopBlockPos(world, pos);
        if (bottom == null || top == null) return List.of();
        List<BlockPos> list = new ArrayList<>();
        for (int y = bottom.getY(); y <= top.getY(); y++) {
            list.add(new BlockPos(bottom.getX(), y, bottom.getZ()));
        }
        return list;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
        if (state.get(POLE_STATE).equals(BlockStateProperties.FlagPoleState.TOP)) {
            shape = VoxelShapes.union(shape, Block.createCuboidShape(5, 14, 5, 11, 16, 11));
        }
        return shape;
    }

    @Override
    protected BlockSoundGroup getSoundGroup(BlockState state) {
        return BlockSoundGroup.ANVIL;
    }
}
