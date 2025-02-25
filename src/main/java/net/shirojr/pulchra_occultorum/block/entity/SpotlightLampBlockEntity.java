package net.shirojr.pulchra_occultorum.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.init.BlockEntities;
import net.shirojr.pulchra_occultorum.init.Blocks;
import net.shirojr.pulchra_occultorum.init.Tags;
import net.shirojr.pulchra_occultorum.network.packet.SpotlightSoundPacket;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;
import net.shirojr.pulchra_occultorum.util.ShapeUtil;
import net.shirojr.pulchra_occultorum.util.SoundOrigin;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SpotlightLampBlockEntity extends AbstractTickingBlockEntity implements ExtendedScreenHandlerFactory<SpotlightLampBlockEntity.Data>, SoundOrigin {
    public static final float MAX_TURNING_SPEED = 0.7f;
    public static final float MIN_PITCH_RANGE = -90, MAX_PITCH_RANGE = 50;
    public static final float MIN_YAW_RANGE = -180, MAX_YAW_RANGE = 180;

    private ShapeUtil.Position rotation, targetRotation;
    private int strength = 0;
    private float speed = 0;
    private boolean isRotating = false;
    private SimpleInventory inventory = new SimpleInventory(1);

    public SpotlightLampBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.SPOTLIGHT_LAMP_BLOCK_ENTITY, pos, state);
        this.rotation = new ShapeUtil.Position(0, 0);
        this.targetRotation = new ShapeUtil.Position(0, 0);
    }

    public Data createData() {
        return new Data(this.getCachedState().get(Properties.POWER),
                getRotation(), getTargetRotation(), this.getPos(), this.getSpeed());
    }

    public static void tick(World world, BlockPos pos, BlockState state, SpotlightLampBlockEntity blockEntity) {
        blockEntity.incrementTick(false);

        blockEntity.setStrength(getPowerFromBase(world, pos, blockEntity));
        if (world.isClient()) return;
        if (blockEntity.getStrength() <= 0) blockEntity.setSpeed(0.0f);

        blockEntity.rotationHandling();
        boolean changeSoundState = false;
        if (blockEntity.isRotating) {
            if (blockEntity.getRotation().equals(blockEntity.getTargetRotation()) || blockEntity.getSpeed() <= 0) {
                changeSoundState = true;
                blockEntity.isRotating = false;
                // blockEntity.setSpeed(0.0f);
            }
        } else {
            if (!blockEntity.getRotation().equals(blockEntity.getTargetRotation()) && blockEntity.getSpeed() > 0) {
                changeSoundState = true;
                blockEntity.isRotating = true;
            }
        }
        if (changeSoundState) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
                new SpotlightSoundPacket(pos, blockEntity.isRotating).sendPacket(player);
            }
        }
    }

    private void rotationHandling() {
        ShapeUtil.Position rotation = this.getRotation();
        ShapeUtil.Position targetRotation = this.getTargetRotation();

        if (rotation.equals(targetRotation)) return;
        if (this.getSpeed() <= 0) return;

        float newX = rotation.getX() + this.getSpeed(), newY = rotation.getY() + this.getSpeed();
        if (newX < targetRotation.getX()) {
            newX = Math.min(rotation.getX() + this.getSpeed(), targetRotation.getX());
        }
        if (newX > targetRotation.getX()) {
            newX = Math.max(rotation.getX() - this.getSpeed(), targetRotation.getX());
        }
        if (newY < targetRotation.getY()) {
            newY = Math.min(rotation.getY() + this.getSpeed(), targetRotation.getY());
        }
        if (newY > targetRotation.getY()) {
            newY = Math.max(rotation.getY() - this.getSpeed(), targetRotation.getY());
        }
        this.setRotation(new ShapeUtil.Position(newX, newY));
    }

    //region getter & setter
    private static int getPowerFromBase(World world, BlockPos originalPos, SpotlightLampBlockEntity blockEntity) {
        if (!world.isChunkLoaded(ChunkSectionPos.getSectionCoord(originalPos.getX()),
                ChunkSectionPos.getSectionCoord(originalPos.getZ()))) return 0;

        BlockPos.Mutable posWalker = originalPos.mutableCopy();

        do posWalker.move(Direction.DOWN);
        while (world.getBlockState(posWalker).isIn(Tags.Blocks.SENDS_UPDATE_POWER_VERTICALLY));

        BlockState baseBlockState = world.getBlockState(posWalker);
        int receivedPower = baseBlockState.isSolidBlock(world, posWalker) ? world.getReceivedRedstonePower(posWalker) : 0;
        int blockStatePower = baseBlockState.contains(Properties.POWER) ? world.getBlockState(posWalker).get(Properties.POWER) : 0;
        int finalPower = Math.max(receivedPower, blockStatePower);
        if (world.getBlockState(originalPos).contains(Properties.POWER) &&
                world.getBlockState(originalPos).get(Properties.POWER) != finalPower &&
                !world.isClient()) {
            // FIXME: also send update when the block gets changed?
            world.updateNeighbor(originalPos, Blocks.SPOTLIGHT_LAMP, posWalker);
        }
        return finalPower;
    }

    public ItemStack getColorStack() {
        if (this.inventory.isEmpty()) return ItemStack.EMPTY;
        return this.inventory.getStack(0);
    }

    public void setColorStack(ItemStack stack) {
        this.inventory.setStack(0, stack.copyWithCount(1));
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
            markDirty();
        }
    }

    public void clearInventory(boolean dropInventory, BlockPos spawnPos) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!this.getColorStack().isEmpty() && dropInventory) {
            ItemScatterer.spawn(serverWorld, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), this.getColorStack());
        }
        setColorStack(ItemStack.EMPTY);
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
            markDirty();
        }
    }

    public ShapeUtil.Position getRotation() {
        return rotation;
    }

    public void setRotation(ShapeUtil.Position rotation) {
        this.rotation = rotation;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
            markDirty();
        }
    }

    public ShapeUtil.Position getTargetRotation() {
        return targetRotation;
    }

    public void setTargetRotation(Supplier<ShapeUtil.Position> targetRotationConsumer) {
        this.targetRotation = targetRotationConsumer.get();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
            markDirty();
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
            markDirty();
        }
    }
    //endregion

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = new SimpleInventory(1);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);

        this.speed = nbt.getFloat("speed");

        NbtCompound rotationNbt = nbt.getCompound("rotation");
        NbtCompound targetRotationNbt = nbt.getCompound("target_rotation");
        this.rotation = ShapeUtil.Position.fromNbt(rotationNbt);
        this.targetRotation = ShapeUtil.Position.fromNbt(targetRotationNbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.putFloat("speed", this.getSpeed());

        NbtCompound rotationNbt = new NbtCompound();
        NbtCompound targetRotationNbt = new NbtCompound();
        this.rotation.toNbt(rotationNbt);
        this.targetRotation.toNbt(targetRotationNbt);
        nbt.put("rotation", rotationNbt);
        nbt.put("target_rotation", targetRotationNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public Data getScreenOpeningData(ServerPlayerEntity player) {
        return createData();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SpotlightLampScreenHandler(syncId, playerInventory, this.createData());
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(this.hashCode());
    }

    @Override
    public Vec3d getSoundPos() {
        return this.getPos().toCenterPos();
    }

    @Override
    public @Nullable Vec3d getVelocity() {
        return null;
    }

    @Override
    public boolean stoppedExisting() {
        return this.isRemoved();
    }


    public record Data(int strength, ShapeUtil.Position rotation, ShapeUtil.Position targetRotation,
                       BlockPos pos, float speed) implements CustomPayload {

        public static final CustomPayload.Id<Data> IDENTIFIER = new CustomPayload.Id<>(PulchraOccultorum.identifierOf("spotlight_lamp_data"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return IDENTIFIER;
        }

        public static final PacketCodec<RegistryByteBuf, Data> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, Data::strength,
                ShapeUtil.Position.CODEC_POSITION, Data::rotation,
                ShapeUtil.Position.CODEC_POSITION, Data::targetRotation,
                BlockPos.PACKET_CODEC, Data::pos,
                PacketCodecs.FLOAT, Data::speed,
                Data::new
        );
    }
}
