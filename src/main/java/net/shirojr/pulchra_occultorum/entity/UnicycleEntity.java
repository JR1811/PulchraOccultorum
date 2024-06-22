package net.shirojr.pulchra_occultorum.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleSoundPacket;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractRideableEntity;
import org.jetbrains.annotations.Nullable;

public class UnicycleEntity extends AbstractRideableEntity {
    private static final float JUMP_STRENGTH = 1.5f;
    private boolean hasMovementInputs = false;

    public UnicycleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 5);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        ServerPlayNetworking.send(player, new UnicycleSoundPacket(this.getId(), true));
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        ServerPlayNetworking.send(player, new UnicycleSoundPacket(this.getId(), false));
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.UNICYCLE_CLANK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.UNICYCLE_CLANK;
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        float sideways = controllingPlayer.sidewaysSpeed * 0.2F;
        float forward = controllingPlayer.forwardSpeed;
        float up = controllingPlayer.upwardSpeed;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        Vec3d controlledMovement = new Vec3d(sideways, up, forward);
        setMovedByInputs(controlledMovement.length() > 0);
        return controlledMovement;
    }

    public boolean isMovedByInput() {
        return this.hasMovementInputs;
    }

    public void setMovedByInputs(boolean usesInputsToMove) {
        this.hasMovementInputs = usesInputsToMove;
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        this.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();

        if (!this.isLogicalSideForUpdatingMovement()) return;
        if (!this.isOnGround()) return;
        this.setInAir(false);
        if (movementInput.length() > 0) {
            this.move(movementInput);
        }
    }

    protected void move(Vec3d movementInput) {
        float speed = 0.5f;
        this.velocityDirty = true;

        if (movementInput.z > 0.0) {
            float zDirection = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
            float xDirection = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
            float jumpStrength = this.getJumpVelocity(UnicycleEntity.JUMP_STRENGTH);
            if (!MinecraftClient.getInstance().options.jumpKey.isPressed()) jumpStrength = 0.0f;
            if (this.isInAir() && !this.isLogicalSideForUpdatingMovement()) jumpStrength = 0.0f;
            if (jumpStrength > 0.0f) {
                this.setInAir(true);
                speed += 0.8f;
            }
            this.setVelocity(new Vec3d(-speed * zDirection, jumpStrength, speed * xDirection));
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        float computedFallDamage = this.computeFallDamage(fallDistance, damageMultiplier) * 0.8f;
        this.playSound(this.getLandingSound(), 1.2F, 0.9F);
        if (computedFallDamage > 0) {
            this.playBlockFallSound();
            for (Entity passenger : this.getPassengerList()) {
                if (!(passenger instanceof LivingEntity livingEntity)) continue;
                if (livingEntity.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) continue;
                livingEntity.damage(damageSource, computedFallDamage);
            }
            return true;
        } else {
            return false;
        }
    }

    public SoundEvent getLandingSound() {
        return SoundEvents.UNICYCLE_LAND;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // super.playStepSound(pos, state);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }
}
