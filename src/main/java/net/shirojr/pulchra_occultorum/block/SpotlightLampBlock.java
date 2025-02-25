package net.shirojr.pulchra_occultorum.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.init.Tags;
import org.jetbrains.annotations.Nullable;

public class SpotlightLampBlock extends BlockWithEntity {
    public static final MapCodec<SpotlightLampBlock> CODEC = createCodec(SpotlightLampBlock::new);

    public SpotlightLampBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(Properties.POWER, 0)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWER, Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(Properties.POWER, 0)
                .with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
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
        if (player.isBlockBreakingRestricted(world, pos, GameMode.ADVENTURE)) return ActionResult.PASS;

        if (player.getMainHandStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof Stainable) {
            return super.onUse(state, world, pos, player, hit);
        }
        if (world.getBlockEntity(pos) instanceof SpotlightLampBlockEntity blockEntity) {
            if (blockEntity.getStrength() <= 0) {
                if (!world.isClient()) player.sendMessage(Text.translatable("chat.pulchra-occultorum.missing_power"), true);
                return ActionResult.PASS;
            }
            if (player.isSneaking()) {
                if (player.getMainHandStack().isEmpty() && !blockEntity.getColorStack().isEmpty()) {
                    if (!world.isClient()) {
                        blockEntity.clearInventory(true, pos);
                        world.playSound(null, blockEntity.getPos(),
                                SoundEvents.BLOCK_COPPER_GRATE_BREAK, SoundCategory.BLOCKS, 2.0f, 0.8f);
                    }
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        }

        if (!world.isClient()) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof SpotlightLampBlockEntity blockEntity) {
            blockEntity.clearInventory(true, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isBlockBreakingRestricted(world, pos, GameMode.ADVENTURE)) return ItemActionResult.FAIL;
        if (!(world.getBlockEntity(pos) instanceof SpotlightLampBlockEntity blockEntity)) return ItemActionResult.FAIL;
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof Stainable)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        blockEntity.clearInventory(true, pos);
        if (blockEntity.getColorStack().isEmpty() || !blockEntity.getColorStack().isOf(blockItem)) {
            blockEntity.setColorStack(stack);
            world.playSound(null, blockEntity.getPos(),
                    SoundEvents.BLOCK_COPPER_GRATE_PLACE, SoundCategory.BLOCKS, 2.0f, 1.2f);
        }
        if (!player.isCreative()) {
            stack.decrement(1);
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        int power = getPotentialPowerFromSource(world, pos);
        world.setBlockState(pos, state.with(Properties.POWER, power), NOTIFY_ALL_AND_REDRAW);

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            int power = getPotentialPowerFromSource(world, pos);
            world.setBlockState(pos, state.with(Properties.POWER, power), NOTIFY_ALL_AND_REDRAW);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public static int getPotentialPowerFromSource(WorldAccess world, BlockPos originalPos) {
        BlockPos posWalker = originalPos.down();
        while (world.getBlockState(posWalker).isIn(Tags.Blocks.SENDS_UPDATE_POWER_VERTICALLY)) {
            posWalker = posWalker.down();
        }
        return world.getReceivedRedstonePower(posWalker);
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
        return Block.createCuboidShape(6, 0, 6, 10, 12, 10);
    }

    public static int luminanceFromBlockState(BlockState state) {
        if (!state.contains(Properties.POWER)) return 0;
        return state.get(Properties.POWER);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
