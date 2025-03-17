package net.shirojr.pulchra_occultorum.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleSoundPacket;
import net.shirojr.pulchra_occultorum.util.SoundOrigin;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractRideableEntity;
import org.jetbrains.annotations.Nullable;

public class UnicycleEntity extends AbstractRideableEntity implements SoundOrigin {
    public static final float JUMP_STRENGTH = 1.5f, INTERVAL_SPEED = 0.25f;

    private boolean hasMovementInputs = false;
    private static final TrackedData<Float> LEFT_IMPORTANT_STATE = DataTracker.registerData(UnicycleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Nullable
    private DirectionInput[] directionInputs = new DirectionInput[]{null, null, null};

    public UnicycleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 5);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(LEFT_IMPORTANT_STATE, 0f);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        new UnicycleSoundPacket(this.getId(), true).sendPacket(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        new UnicycleSoundPacket(this.getId(), false).sendPacket(player);
    }

    public DirectionInput[] getDirectionInputs() {
        return this.directionInputs;
    }

    public void setDirectionInputs(@Nullable DirectionInput inputLeft, @Nullable DirectionInput inputRight) {
        this.setDirectionInputs(new DirectionInput[]{inputLeft, inputRight});
    }

    public void setDirectionInputs(@Nullable DirectionInput[] input) {
        this.directionInputs = input;
    }

    public float getLeftImportantState() {
        return this.dataTracker.get(LEFT_IMPORTANT_STATE);
    }

    public void setLeftImportantState(float state) {
        state = MathHelper.clamp(state, -1, 1);
        this.dataTracker.set(LEFT_IMPORTANT_STATE, state);
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
        this.move(movementInput);

    }

    @Override
    public void onLanding() {
        super.onLanding();
        this.setInAir(false);
    }

    protected void move(Vec3d movementInput) {
        float speed = 0;
        DirectionInput[] input = this.getDirectionInputs();
        DirectionInput left = input[0];
        DirectionInput right = input[1];
        DirectionInput jump = input[2];
        if (left == null && right == null && jump == null) return;

        boolean goesForward = false;
        boolean goesBackward = false;
        boolean jumped = false;

        if (left != null) {
            goesForward = left.equals(DirectionInput.LEFT) && this.getLeftImportantState() > 0;
            goesBackward = left.equals(DirectionInput.LEFT) && this.getLeftImportantState() < 0;
        }
        if (right != null) {
            goesForward = right.equals(DirectionInput.RIGHT) && this.getLeftImportantState() < 0;
            goesBackward = right.equals(DirectionInput.RIGHT) && this.getLeftImportantState() > 0;
        }
        if (jump != null) {
            jumped = jump.equals(DirectionInput.JUMP) && !this.isInAir();   // maybe use onGround instead?
        }

        if (goesForward) speed = 0.2f;
        if (goesBackward) speed = -0.2f;

        if (speed == 0 && !jumped) return;
        if (this.isTouchingWater()) {
            speed *= 0.15f;
        }

        this.velocityDirty = true;
        if (this.isInAir()) return;
        float zDirection = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
        float xDirection = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
        float jumpStrength = jumped ? this.getJumpVelocity(UnicycleEntity.JUMP_STRENGTH) : 0.0f;
        if (jumpStrength > 0.0f) {
            this.setInAir(true);
            speed += 0.8f;
        }
        this.addVelocity(new Vec3d(-speed * zDirection, jumpStrength, speed * xDirection));
        // this.directionInputs = new DirectionInput[] {null, null, null};
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

    @Override
    public String getUniqueId() {
        return this.getUuid().toString();
    }

    @Override
    public World getSoundOriginWorld() {
        return this.getWorld();
    }

    @Override
    public Vec3d getSoundPos() {
        return this.getPos();
    }

    @Override
    public @Nullable Vec3d getSoundOriginVelocity() {
        return this.getVelocity();
    }

    @Override
    public boolean stoppedExisting() {
        return this.isRemoved() || this.isDead();
    }

    public enum DirectionInput {
        LEFT("input_left"),
        RIGHT("input_right"),
        JUMP("input_jump");

        private final String name;

        DirectionInput(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static DirectionInput fromString(String name) {
            for (DirectionInput entry : DirectionInput.values()) {
                if (entry.getName().equals(name)) return entry;
            }
            return null;
        }
    }
}
