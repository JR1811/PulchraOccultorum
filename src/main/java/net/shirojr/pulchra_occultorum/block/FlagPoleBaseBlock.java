package net.shirojr.pulchra_occultorum.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
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
import net.shirojr.pulchra_occultorum.block.entity.FlagPoleBlockEntity;
import net.shirojr.pulchra_occultorum.util.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlagPoleBaseBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public FlagPoleBaseBlock(Settings settings) {
        super(settings);
        this.stateManager.getDefaultState().with(WATERLOGGED, false);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        FlagPoleBlockEntity blockEntity = isValidFlagStructure(hit, world);
        if (blockEntity == null) return super.onUse(state, world, pos, player, hit);
        if (blockEntity.dropFlagInventory(player)) return ActionResult.SUCCESS;
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isIn(ItemTags.BANNERS)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        FlagPoleBlockEntity blockEntity = isValidFlagStructure(hit, world);
        if (blockEntity == null) return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        blockEntity.addOrReplaceFlagItemStack(player, stack.copyWithCount(1));
        if (!player.isCreative()) stack.decrement(1);
        world.playSound(null, blockEntity.getPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return ItemActionResult.SUCCESS;
    }

    @Nullable
    private FlagPoleBlockEntity isValidFlagStructure(BlockHitResult hit, World world) {
        BlockPos flagPolePos = hit.getBlockPos().up();
        flagPolePos = FlagPoleBlock.getBaseBlockPos(world, flagPolePos);
        if (flagPolePos == null) return null;
        flagPolePos = flagPolePos.up();
        if (!world.getBlockState(flagPolePos).contains(BlockStateProperties.FLAG_POLE_STATE))
            return null;
        if (!(world.getBlockEntity(flagPolePos) instanceof FlagPoleBlockEntity flagPoleBlockEntity)) return null;
        return flagPoleBlockEntity;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 1, 0.8125));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.pulchra-occultorum.flag_pole_base.tooltip1"));
        tooltip.add(Text.translatable("block.pulchra-occultorum.flag_pole_base.tooltip2"));
    }
}
