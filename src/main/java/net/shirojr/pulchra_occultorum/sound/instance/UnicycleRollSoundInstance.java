package net.shirojr.pulchra_occultorum.sound.instance;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class UnicycleRollSoundInstance extends MovingSoundInstance {
    protected final UnicycleEntity entity;
    protected float clampedDistance = 0.0f;
    protected int currentTick = 0, transitionTick = 0;
    protected final int startTransitionTicks, endTransitionTicks;
    protected TransitionState transitionState;
    protected boolean isFinished = false;

    private static final float MAX_DISTANCE = 20;

    public UnicycleRollSoundInstance(UnicycleEntity entity, int startTransitionTicks, int endTransitionTicks) {
        super(SoundEvents.UNICYCLE_ROLL, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        this.entity = entity;
        this.repeat = true;
        this.volume = 0.0f;
        this.transitionState = TransitionState.STARTING;
        this.startTransitionTicks = startTransitionTicks;
        this.endTransitionTicks = endTransitionTicks;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public void tick() {
        if (entity.getWorld().getTickManager().shouldTick()) this.currentTick++;
        else return;
        if (this.entity.isRemoved() || entity.isDead()) {
            this.finishSoundInstance();
        }

        switch (this.transitionState) {
            case STARTING:
                this.transitionTick++;
                if (this.transitionTick >= startTransitionTicks) {
                    this.transitionState = TransitionState.IDLE;
                    this.transitionTick = 0;
                }
            case FINISHING:
                if (this.transitionTick < endTransitionTicks) this.transitionTick++;
                else this.isFinished = true;
        }

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();

        if (isFinished()) {
            this.transitionState = TransitionState.STARTING;
            this.currentTick = 0;
            this.transitionTick = 0;
            this.setDone();
        }

        defaultSoundHandling(this);
    }

    protected static void defaultSoundHandling(UnicycleRollSoundInstance soundInstance) {
        boolean shouldTick = soundInstance.entity.getWorld().getTickManager().shouldTick();
        if (!shouldTick) {
            soundInstance.clampedDistance = 0.0f;
            soundInstance.volume = 0.0f;
            soundInstance.pitch = 1.0f;
        } else {
            transformSoundForDistance(soundInstance);
            transformSoundForMoving(soundInstance);
            transformSoundForTransition(soundInstance.volume, soundInstance.pitch, soundInstance, true, true);
        }
    }

    protected static void transformSoundForMoving(UnicycleRollSoundInstance soundInstance) {
        double horizontalVelocity = soundInstance.entity.getVelocity().horizontalLength();
        if (horizontalVelocity <= 0) {
            soundInstance.volume = 0.0f;
        } else {
            soundInstance.pitch = (float) MathHelper.lerp(horizontalVelocity, 0.9f, 1.2f);
        }
    }

    protected static void transformSoundForDistance(UnicycleRollSoundInstance soundInstance) {
        if (!(MinecraftClient.getInstance().player instanceof ClientPlayerEntity player)) return;

        soundInstance.clampedDistance = (float) Math.clamp(soundInstance.entity.getPos().distanceTo(player.getPos()), 0.0, MAX_DISTANCE);
        float normalizedIntensity = (MAX_DISTANCE - soundInstance.clampedDistance) / MAX_DISTANCE;

        soundInstance.volume = MathHelper.lerp(normalizedIntensity, 0.0f, 1.3f);
        if (soundInstance.clampedDistance > 0) {
            LoggerUtil.devLogger(String.valueOf(soundInstance.clampedDistance));
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected static void transformSoundForTransition(float originalVolume, float originalPitch, UnicycleRollSoundInstance soundInstance,
                                                      boolean includeStarting, boolean includeStopping) {
        float normalizedStartTransitionTick = (float) soundInstance.transitionTick / soundInstance.startTransitionTicks;
        float normalizedEndTransitionTick = (float) soundInstance.transitionTick / soundInstance.endTransitionTicks;
        switch (soundInstance.transitionState) {
            case STARTING -> {
                if (includeStarting) {
                    soundInstance.volume = MathHelper.lerp(normalizedStartTransitionTick, 0.0f, originalVolume);
                    soundInstance.pitch = MathHelper.lerp(normalizedStartTransitionTick, originalPitch - 0.2f, originalPitch);
                }
            }
            case FINISHING -> {
                if (includeStopping) {
                    soundInstance.volume = MathHelper.lerp(normalizedEndTransitionTick, originalVolume, 0.0f);
                    soundInstance.pitch = MathHelper.lerp(normalizedEndTransitionTick, originalPitch, originalPitch - 0.2f);
                }
            }
        }
    }

    public void finishSoundInstance() {
        this.transitionState = TransitionState.FINISHING;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    protected enum TransitionState {STARTING, FINISHING, IDLE}
}
