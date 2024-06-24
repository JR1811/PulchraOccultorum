package net.shirojr.pulchra_occultorum.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.block.FlagPoleBlock;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.util.BlockStateProperties;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FlagPoleBlockEntity extends AbstractTickingBlockEntity {
    private final SimpleInventory flagInventory;
    private boolean hoisted = false;
    private float hoistedState = 0.0f;
    @Nullable
    private BlockPos baseBlockPos = null;

    public FlagPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.FLAG_POLE_BLOCK_ENTITY, pos, state);
        this.flagInventory = new SimpleInventory(1);
        if (this.getWorld() == null) return;
        setBaseBlockPos(FlagPoleBlock.getBaseBlockPos(this.getWorld(), pos));
    }

    public static void tick(World world, BlockPos pos, BlockState state, FlagPoleBlockEntity blockEntity) {
        if (!state.contains(BlockStateProperties.FLAG_POLE_STATE) ||
                !state.get(BlockStateProperties.FLAG_POLE_STATE).equals(BlockStateProperties.FlagPoleState.TOP))
            return;

        blockEntity.incrementTick(false);
        blockEntity.modifyHoistedState(world, pos, 0.01f);
        if (world instanceof ServerWorld serverWorld) {
            if (blockEntity.getTick() % 10 == 0 && blockEntity.isHoistStateMoving()) {
                if (blockEntity.getBaseBlockPos() != null && blockEntity.getTopBlockPos() != null) {
                    BlockPos soundPos =
                            new BlockPos(
                                    blockEntity.getTopBlockPos().getX(),
                                    MathHelper.lerp(blockEntity.getHoistedState(), blockEntity.getBaseBlockPos().getY(), blockEntity.getTopBlockPos().getY()),
                                    blockEntity.getTopBlockPos().getZ());
                    serverWorld.playSound(null, soundPos,
                            SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS,
                            1.0f, MathHelper.lerp(serverWorld.getRandom().nextFloat(), 0.7f, 1.1f));
                }
            }
        }
    }

    //region getter & setter
    public SimpleInventory getFlagInventory() {
        markDirty();
        return this.flagInventory;
    }

    public void setFlagInventory(SimpleInventory inventory) {
        if (inventory.size() != getFlagInventory().size()) return;
        for (int i = 0; i < inventory.getHeldStacks().size(); i++) {
            this.getFlagInventory().setStack(i, inventory.getStack(i));
        }
        markDirty();
    }

    public void setBaseBlockPos(BlockPos baseBlockPos) {
        this.baseBlockPos = baseBlockPos;
    }

    @Nullable
    public BlockPos getBaseBlockPos() {
        return this.baseBlockPos;
    }

    public boolean isHoisted() {
        return hoisted;
    }

    public void setHoisted(boolean hoisted) {
        this.hoisted = hoisted;
        markDirty();
    }

    public float getHoistedState() {
        return Math.clamp(this.hoistedState, 0, 1.0f);
    }

    public boolean isFullyHoisted() {
        return getHoistedState() >= 1.0f;
    }

    public void setHoistedState(float hoistedState) {
        this.hoistedState = Math.clamp(hoistedState, 0, 1.0f);
        markDirty();
    }

    public int getFlagPoleCount() {
        if (this.getWorld() == null) return 0;
        return FlagPoleBlock.getFlagPoleBlockCount(this.getWorld(), this.getPos());
    }

    @Nullable
    public BlockPos getTopBlockPos() {
        if (this.getWorld() == null) return null;
        return FlagPoleBlock.getTopBlockPos(this.getWorld(), this.getPos());
    }

    public boolean isHoistStateMoving() {
        return !isFullyHoisted() && getHoistedState() > 0.0f;
    }
    //endregion

    @SuppressWarnings("SameParameterValue")
    private void modifyHoistedState(World world, BlockPos pos, float speed) {
        float hoistedState = this.getHoistedState();
        BlockPos basePos = FlagPoleBlock.getBaseBlockPos(world, pos);
        if (world.isReceivingRedstonePower(basePos)) {
            if (!this.isFullyHoisted()) {
                hoistedState += speed;
                this.setHoistedState(hoistedState);
            }
        } else if (this.getHoistedState() > 0) {
            hoistedState -= speed;
            this.setHoistedState(hoistedState);
        }

        if (this.getHoistedState() <= 0) {
            if (this.isHoisted()) this.setHoisted(false);
        } else {
            if (!this.isHoisted()) this.setHoisted(true);
        }
        markDirty();
    }

    public boolean dropFlagInventory() {
        if (!(this.getWorld() == null)) return false;
        if (this.getHoistedState() > 0) return false;
        if (this.getFlagInventory().isEmpty()) return false;
        if (this.getBaseBlockPos() == null) return false;
        ItemScatterer.spawn(world, this.getBaseBlockPos().up().north(1), this.getFlagInventory());
        this.getFlagInventory().removeStack(0);
        return true;
    }

    public void addOrReplaceFlagItemStack(ItemStack newStack) {
        dropFlagInventory();
        this.getFlagInventory().setStack(0, newStack);
    }

    public static void transferBlockEntityValues(FlagPoleBlockEntity oldBlockEntity, FlagPoleBlockEntity newBlockEntity) {
        newBlockEntity.setHoistedState(oldBlockEntity.getHoistedState());
        newBlockEntity.setFlagInventory(oldBlockEntity.getFlagInventory());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(NbtKeys.FLAG_IS_HOISTED)) {
            setHoisted(nbt.getBoolean(NbtKeys.FLAG_IS_HOISTED));
        }
        if (nbt.contains(NbtKeys.FLAG_HOISTED_STATE)) {
            setHoistedState(Math.clamp(nbt.getFloat(NbtKeys.FLAG_HOISTED_STATE), 0, 1.0f));
        }
        if (nbt.contains(NbtKeys.FLAG_INVENTORY)) {
            Inventories.writeNbt(nbt, this.getFlagInventory().getHeldStacks(), registryLookup);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean(NbtKeys.FLAG_IS_HOISTED, isHoisted());
        nbt.putFloat(NbtKeys.FLAG_HOISTED_STATE, getHoistedState());
        Inventories.readNbt(nbt, this.getFlagInventory().getHeldStacks(), registryLookup);
    }
}
