package net.shirojr.pulchra_occultorum.mixin;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.shirojr.pulchra_occultorum.init.Tags;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin extends State<Block, BlockState> {
    protected AbstractBlockStateMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
        super(owner, propertyMap, codec);
    }

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Shadow
    public abstract BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);

    @Inject(method = "getStateForNeighborUpdate", at = @At("TAIL"))
    private void pulchraOccultorum$extendUpdatesVertically(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        BlockPos posWalkerUp = pos, posWalkerDown = pos;

        do {
            posWalkerUp = posWalkerUp.up();
            // LoggerUtil.devLogger("went up to: %s | BlockState: %s".formatted(posWalkerUp, world.getBlockState(posWalkerUp)));
        }
        while (world.getBlockState(posWalkerUp).isIn(Tags.Blocks.SENDS_UPDATE_POWER_VERTICALLY));
        posWalkerUp = posWalkerUp.up(); // stop on top of last block

        do {
            posWalkerDown = posWalkerDown.down();
            // LoggerUtil.devLogger("went down to: %s | BlockState: %s".formatted(posWalkerDown, world.getBlockState(posWalkerDown)));
        }
        while (world.getBlockState(posWalkerDown).isIn(Tags.Blocks.SENDS_UPDATE_POWER_VERTICALLY));
        posWalkerDown = posWalkerDown.down(); // stop below last block

        Registries.BLOCK.stream().filter(block -> block.getDefaultState().isIn(Tags.Blocks.SENDS_UPDATE_POWER_VERTICALLY)).forEach(block -> LoggerUtil.devLogger(String.valueOf(block)));

        if (pos.getX() == posWalkerUp.getX() && pos.getZ() == posWalkerUp.getZ()) {
            if (!world.isOutOfHeightLimit(posWalkerUp) && !pos.equals(posWalkerUp.down(2))) {
                getStateForNeighborUpdate(Direction.DOWN, world.getBlockState(pos), world, posWalkerUp, pos);
            }
        }
        if (pos.getX() == posWalkerDown.getX() && pos.getZ() == posWalkerDown.getZ()) {
            if (!world.isOutOfHeightLimit(posWalkerDown) && !pos.equals(posWalkerDown.up(2))) {
                getStateForNeighborUpdate(Direction.UP, world.getBlockState(pos), world, posWalkerDown, pos);
            }
        }

        getBlock().getStateForNeighborUpdate(asBlockState(), direction, neighborState, world, pos, neighborPos);
    }
}
