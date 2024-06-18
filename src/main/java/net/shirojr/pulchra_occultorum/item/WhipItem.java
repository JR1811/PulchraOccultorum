package net.shirojr.pulchra_occultorum.item;

import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.CustomDataComponents;
import net.shirojr.pulchra_occultorum.util.Fright;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingItem;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhipItem extends AbstractTickingItem {
    public WhipItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient()) {
            startTicking(stack);
            if (user.getVehicle() instanceof AbstractHorseEntity horseEntity) {
                horseEntity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED, 100, 2, false, false, false), user);
                horseEntity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 40, 2, false, false, false), horseEntity);

                horseEntity.damage(world.getDamageSources().generic(), 2);
                horseEntity.setAngry(false);
            }

            List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(5),
                    livingEntity -> !livingEntity.equals(user));
            for (LivingEntity nearbyEntity : nearbyEntities) {
                if (!(nearbyEntity instanceof Fright frightenedMob)) continue;
                frightenedMob.pulchraOccultorum$setFrightenedTicksLeft(Fright.MAX_FRIGHTENED_TICKS);
                frightenedMob.pulchraOccultorum$setAggressor(user);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        ItemStack oldTemp = oldStack.copy();
        ItemStack newTemp = newStack.copy();
        var changes = ComponentChanges.builder().remove(CustomDataComponents.TICK).build();
        oldTemp.applyChanges(changes);
        newTemp.applyChanges(changes);
        return !oldStack.getComponents().stream().allMatch(component -> newStack.contains(component.type()));
    }

    @Override
    public int getMaxTick(ItemStack stack) {
        return 10;
    }


    public static class WhipUsedGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final PathAwareEntity fleeingEntity;
        @Nullable
        private final LivingEntity attackingEntity;

        public WhipUsedGoal(PathAwareEntity fleeingEntity, @Nullable LivingEntity attackingEntity, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(fleeingEntity, fleeFromType, distance, slowSpeed, fastSpeed);
            this.fleeingEntity = fleeingEntity;
            this.attackingEntity = attackingEntity;
        }

        @Override
        public boolean canStart() {
            if (!fleeingEntity.getPassengerList().isEmpty()) return false;
            if (this.attackingEntity == null) return false;
            if (!(this.fleeingEntity instanceof Fright frightenedEntity)) return false;
            if (!frightenedEntity.pulchraOccultorum$isFrightened()) return false;

            Vec3d vec3d = NoPenaltyTargeting.findFrom(this.fleeingEntity, 16, 7, this.attackingEntity.getPos());
            if (vec3d == null) {
                return false;
            } else if (this.attackingEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.attackingEntity.squaredDistanceTo(this.fleeingEntity)) {
                return false;
            } else {
                this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.fleePath != null;
            }
        }

        @Override
        public boolean shouldContinue() {
            if (!((Fright) this.fleeingEntity).pulchraOccultorum$isFrightened()) return false;
            if (this.attackingEntity == null) return false;
            return super.shouldContinue();
        }

        @Override
        public void tick() {
            super.tick();
            this.fleeingEntity.setTarget(null);
            LoggerUtil.devLogger("ticked");
        }

        @Override
        public boolean canStop() {
            if (this.fleeingEntity instanceof Fright frightenedMob) {
                if (frightenedMob.pulchraOccultorum$isFrightened()) return false;
            }
            return super.canStop();
        }
    }
}
