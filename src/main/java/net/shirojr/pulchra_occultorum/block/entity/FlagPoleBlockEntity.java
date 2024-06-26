package net.shirojr.pulchra_occultorum.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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

import java.util.List;
import java.util.function.Consumer;

public class FlagPoleBlockEntity extends AbstractTickingBlockEntity {
    private static final int invSize = 1;
    private final SimpleInventory flagInventory = new SimpleInventory(invSize);
    private boolean hoisted = false;
    private float hoistedState = 0.0f;

    @Environment(EnvType.CLIENT)
    public float flagAnimationProgress = 0;

    public FlagPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.FLAG_POLE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FlagPoleBlockEntity blockEntity) {
        if (!state.contains(BlockStateProperties.FLAG_POLE_STATE)) return;
        if (world.getBlockState(pos.down()).getBlock() instanceof FlagPoleBlock) return;
        blockEntity.incrementTick(false);
        if (blockEntity.getFlagInventory().isEmpty()) return;
        blockEntity.modifyHoistedState(world, pos, 0.01f);
        if (world instanceof ServerWorld serverWorld) {
            if (blockEntity.getTick() % 10 == 0 && blockEntity.isHoistStateMoving()) {
                if (blockEntity.getBaseBlockPos() != null && blockEntity.getTopBlockPos() != null) {
                    BlockPos soundPos = new BlockPos(
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

    public void syncedInventoryModification(Consumer<SimpleInventory> inventoryConsumer) {
        inventoryConsumer.accept(this.getFlagInventory());
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
        }
    }

    @Nullable
    public BlockPos getBaseBlockPos() {
        if (this.getWorld() == null) return null;
        return FlagPoleBlock.getBaseBlockPos(this.getWorld(), this.getPos());
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

    @Nullable
    public BlockPos getFlagPos() {
        if (this.getWorld() == null) return null;
        List<BlockPos> connectedFlagPoleBlocks = FlagPoleBlock.connectedFlagPoleBlocks(world, pos);
        if (connectedFlagPoleBlocks.isEmpty()) return null;
        int getFlagY = MathHelper.lerp(this.getHoistedState(), connectedFlagPoleBlocks.getFirst().getY(), connectedFlagPoleBlocks.getLast().getY());
        return new BlockPos(this.getPos().getX(), getFlagY, this.getPos().getZ());
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

    public boolean dropFlagInventory(PlayerEntity player) {
        if (this.getWorld() == null) return false;
        if (this.isHoisted()) {
            player.sendMessage(Text.translatable("chat.pulchra-occultorum.flag_still_hoisted"), true);
            return false;
        }
        if (this.getFlagInventory().isEmpty()) return false;
        if (this.getBaseBlockPos() == null) return false;
        this.syncedInventoryModification(inventory -> {
            ItemScatterer.spawn(world, this.getBaseBlockPos().up().north(1), inventory);
            inventory.removeStack(0);
        });
        this.getWorld().playSound(null, this.getPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.BLOCKS, 1.0f, 0.7f);
        return true;
    }

    public void addOrReplaceFlagItemStack(PlayerEntity user, ItemStack newStack) {
        this.dropFlagInventory(user);
        this.syncedInventoryModification(inventory -> inventory.setStack(0, newStack));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt, registryLookup);
        return nbt;
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
            this.flagInventory.readNbtList(nbt.getList(NbtKeys.FLAG_INVENTORY, NbtElement.COMPOUND_TYPE), registryLookup);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean(NbtKeys.FLAG_IS_HOISTED, isHoisted());
        nbt.putFloat(NbtKeys.FLAG_HOISTED_STATE, getHoistedState());
        nbt.put(NbtKeys.FLAG_INVENTORY, this.flagInventory.toNbtList(registryLookup));
    }
}
