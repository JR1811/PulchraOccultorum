package net.shirojr.pulchra_occultorum.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingBlockEntity;
import net.shirojr.pulchra_occultorum.util.NbtKeys;

public class SpotlightLampBlockEntity extends AbstractTickingBlockEntity {
    public static final int TURNING_SPEED = 20; // TODO: cap the turning speed to a max number
    private float xAngle = 0, yAngle = 0; // TODO: can be controlled with mouse input
    private int color = 0x000000;
    private boolean isLit = false;

    public SpotlightLampBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.SPOTLIGHT_LAMP_BLOCK_ENTITY, pos, state);
    }

    public boolean isLit() {
        return this.isLit;
    }

    public void setLit(boolean lit) {
        this.isLit = lit;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SpotlightLampBlockEntity blockEntity) {

    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.xAngle = nbt.getFloat(NbtKeys.SPOTLIGHT_ANGLE_X);
        this.yAngle = nbt.getFloat(NbtKeys.SPOTLIGHT_ANGLE_Y);
        this.color = nbt.getInt(NbtKeys.SPOTLIGHT_COLOR);
        this.isLit = nbt.getBoolean(NbtKeys.SPOTLIGHT_LIT);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat(NbtKeys.SPOTLIGHT_ANGLE_X, this.xAngle);
        nbt.putFloat(NbtKeys.SPOTLIGHT_ANGLE_Y, this.yAngle);
        nbt.putInt(NbtKeys.SPOTLIGHT_COLOR, this.color);
        nbt.putBoolean(NbtKeys.SPOTLIGHT_LIT, this.isLit);
    }
}
