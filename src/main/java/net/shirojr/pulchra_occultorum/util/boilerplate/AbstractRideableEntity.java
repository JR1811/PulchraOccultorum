package net.shirojr.pulchra_occultorum.util.boilerplate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractRideableEntity extends LivingEntity {
    private boolean inAir;
    protected AbstractRideableEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.inAir = false;
    }

    public void setInAir(boolean inAir) {
        this.inAir = inAir;
    }

    public boolean isInAir() {
        return inAir;
    }

    @Override
    public void tick() {
        super.tick();
/*        if (!this.isOnGround()) {
            if (!isInAir()) {
                onLeavingGround();
            }
            setInAir(true);
        } else {
            if (isInAir()) {

            }
        }*/
    }

    protected void onLeavingGround() {
    }

    @Override
    public void onLanding() {
        super.onLanding();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(NbtKeys.IS_IN_AIR)) {
            setInAir(nbt.getBoolean(NbtKeys.IS_IN_AIR));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean(NbtKeys.IS_IN_AIR, isInAir());
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity ridingEntity = this.getFirstPassenger();
        if (ridingEntity instanceof PlayerEntity player) {
            return player;
        }
        return super.getControllingPassenger();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient()) {
            if (startRiding(player)) {
                return ActionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    public boolean startRiding(PlayerEntity player) {
        player.setYaw(this.getYaw());
        player.setPitch(this.getPitch());
        return player.startRiding(this);
    }


    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
