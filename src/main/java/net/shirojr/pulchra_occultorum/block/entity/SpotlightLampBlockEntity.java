package net.shirojr.pulchra_occultorum.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingBlockEntity;

public class SpotlightLampBlockEntity extends AbstractTickingBlockEntity {
    public static final int TURNING_SPEED = 20; // TODO: cap the turning speed to a max number
    private float currentYaw, currentPitch, targetYaw, targetPitch;
    private int color = 0x000000;
    private boolean isLit = false;

    public SpotlightLampBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.SPOTLIGHT_LAMP_BLOCK_ENTITY, pos, state);
    }

    public boolean isLit() {
        var potentialState = this.getCachedState().getOrEmpty(Properties.LIT);
        return potentialState.orElse(false);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SpotlightLampBlockEntity blockEntity) {
        if (!blockEntity.isLit()) return;
        blockEntity.incrementTick(false);
        if (blockEntity.getTick() >= 360) blockEntity.resetTick();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        // this.lampRotation = Rotation.fromNbt(nbt.getCompound(NbtKeys.SPOTLIGHT_ROTATION));
        this.color = nbt.getInt(NbtKeys.SPOTLIGHT_COLOR);
        this.isLit = nbt.getBoolean(NbtKeys.SPOTLIGHT_LIT);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // this.lampRotation.toNbt(nbt.getCompound(NbtKeys.SPOTLIGHT_ROTATION));
        nbt.putInt(NbtKeys.SPOTLIGHT_COLOR, this.color);
        nbt.putBoolean(NbtKeys.SPOTLIGHT_LIT, this.isLit);
    }
}
