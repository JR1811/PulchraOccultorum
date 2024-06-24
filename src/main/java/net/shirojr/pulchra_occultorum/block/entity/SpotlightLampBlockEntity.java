package net.shirojr.pulchra_occultorum.block.entity;

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
    private Rotation lampRotation = new Rotation(new Rotation.Coordinates(0, 0), new Rotation.Coordinates(0, 0));
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
        blockEntity.incrementTick(false);
        if (blockEntity.getTick() >= 360) blockEntity.setTick(0);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.lampRotation = Rotation.fromNbt(nbt.getCompound(NbtKeys.SPOTLIGHT_ROTATION));
        this.color = nbt.getInt(NbtKeys.SPOTLIGHT_COLOR);
        this.isLit = nbt.getBoolean(NbtKeys.SPOTLIGHT_LIT);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.lampRotation.toNbt(nbt.getCompound(NbtKeys.SPOTLIGHT_ROTATION));
        nbt.putInt(NbtKeys.SPOTLIGHT_COLOR, this.color);
        nbt.putBoolean(NbtKeys.SPOTLIGHT_LIT, this.isLit);
    }

    public static class Rotation {
        private Coordinates current;
        private Coordinates end;
        public Rotation(Coordinates current, Coordinates end) {
            this.current = current;
            this.end = end;
        }

        public void setCurrent(float x, float y) {
            this.current = new Coordinates(x, y);
        }

        public void setEnd(float x, float y) {
            this.end = new Coordinates(x, y);
        }

        public void toNbt(NbtCompound nbt) {
            NbtCompound current = new NbtCompound();
            NbtCompound end = new NbtCompound();
            this.current.toNbt(current);
            this.end.toNbt(end);
            nbt.put("current", current);
            nbt.put("end", end);
        }

        public static Rotation fromNbt(NbtCompound nbt) {
            Coordinates current = Coordinates.fromNbt(nbt.getCompound("current"));
            Coordinates end = Coordinates.fromNbt(nbt.getCompound("end"));
            return new Rotation(current, end);
        }

        public record Coordinates(float x, float y) {
            public void toNbt(NbtCompound nbt) {
                nbt.putFloat("x", x);
                nbt.putFloat("y", y);
            }

            public static Coordinates fromNbt(NbtCompound nbt) {
                return new Coordinates(nbt.getFloat("x"), nbt.getFloat("y"));
            }
        }
    }
}
