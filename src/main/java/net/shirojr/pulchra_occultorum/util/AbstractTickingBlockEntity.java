package net.shirojr.pulchra_occultorum.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.shirojr.pulchra_occultorum.util.handler.TickHandler;

public abstract class AbstractTickingBlockEntity extends BlockEntity implements TickHandler {
    private int tick = 0;

    public AbstractTickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        setTick(nbt.getInt("tick"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("tick", this.getTick());
    }

    @Override
    public int getTick() {
        return tick;
    }

    @Override
    public void setTick(int tick) {
        this.tick = tick;
        markDirty();
    }

    @Override
    public void incrementTick() {
        this.tick++;
    }

    @Override
    public void resetTick() {
        this.tick = 0;
        markDirty();
    }
}
