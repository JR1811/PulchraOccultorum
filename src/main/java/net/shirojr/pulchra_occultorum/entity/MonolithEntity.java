package net.shirojr.pulchra_occultorum.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.block.MonolithBlock;
import net.shirojr.pulchra_occultorum.init.Entities;

import java.util.List;

public class MonolithEntity extends LivingEntity {
    //TODO: origin blockpos datatracker

    public MonolithEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.setNoDrag(true);
        this.setSilent(true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0);
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient() && this.age % 40 == 0) {
            boolean shouldGetDisposed = true;
            for (BlockPos entry : BlockPos.iterateOutwards(this.getBlockPos(), 3, 3, 3)) {
                if (this.getWorld().getBlockState(entry).getBlock() instanceof MonolithBlock) {
                    shouldGetDisposed = false;
                    break;
                }
            }
            if (shouldGetDisposed) this.discard();
        }
        super.tick();
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
