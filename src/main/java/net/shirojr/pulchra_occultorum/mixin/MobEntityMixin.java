package net.shirojr.pulchra_occultorum.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.item.WhipItem;
import net.shirojr.pulchra_occultorum.util.Fright;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements EquipmentHolder, Leashable, Targeter, Fright {
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Unique
    private static final TrackedData<Integer> FRIGHTENED_TICKS = DataTracker.registerData(MobEntityMixin.class, TrackedDataHandlerRegistry.INTEGER);

    @Unique
    @Nullable
    private UUID aggressorUuid = null;


    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addToDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(FRIGHTENED_TICKS, 0);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NbtKeys.AGGRESSOR_UUID)) {
            this.aggressorUuid = nbt.getUuid(NbtKeys.AGGRESSOR_UUID);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.aggressorUuid == null) {
            nbt.remove(NbtKeys.AGGRESSOR_UUID);
        } else {
            nbt.putUuid(NbtKeys.AGGRESSOR_UUID, this.aggressorUuid);
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;initGoals()V"))
    private void addWhipFleeingGoal(CallbackInfo ci) {
        MobEntity fleeingEntity = (MobEntity) (Object) this;
        if (!(fleeingEntity instanceof PathAwareEntity pathAwareEntity)) return;
        goalSelector.add(2, new WhipItem.WhipUsedGoal<>(pathAwareEntity, pulchraOccultorum$getAggressor(),
                LivingEntity.class, 30.0F, 5.0, 10.0));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void addFrightenedTickHandling(CallbackInfo ci) {
        MobEntity fleeingEntity = (MobEntity) (Object) this;
        if (fleeingEntity.getWorld().isClient()) return;
        int frightenedTicksLeft = dataTracker.get(FRIGHTENED_TICKS);
        if (frightenedTicksLeft < 1) return;
        frightenedTicksLeft = frightenedTicksLeft - 1;
        pulchraOccultorum$setFrightenedTicksLeft(frightenedTicksLeft);
        LoggerUtil.devLogger("Frightened: %s | for %s more ticks".formatted(fleeingEntity, frightenedTicksLeft));
    }

    @Override
    public int pulchraOccultorum$getFrightenedTicksLeft() {
        return this.dataTracker.get(FRIGHTENED_TICKS);
    }

    @Override
    public void pulchraOccultorum$setFrightenedTicksLeft(int ticks) {
        this.dataTracker.set(FRIGHTENED_TICKS, ticks);
    }

    @Override
    public boolean pulchraOccultorum$isFrightened() {
        return pulchraOccultorum$getFrightenedTicksLeft() > 0;
    }

    @Override
    @Nullable
    public LivingEntity pulchraOccultorum$getAggressor() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return null;
        Entity entity = serverWorld.getEntity(this.aggressorUuid);
        if (!(entity instanceof LivingEntity livingEntity)) return null;
        return livingEntity;
    }

    @Override
    public void pulchraOccultorum$setAggressor(@Nullable LivingEntity aggressorEntity) {
        if (aggressorEntity == null) this.aggressorUuid = null;
        else this.aggressorUuid = aggressorEntity.getUuid();
    }
}
