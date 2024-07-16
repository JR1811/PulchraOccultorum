package net.shirojr.pulchra_occultorum.util.boilerplate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.SoundOrigin;

public abstract class AbstractDynamicSoundInstance<T extends SoundOrigin> extends MovingSoundInstance {
    protected final int startTransitionTicks, endTransitionTicks;
    private final float maxAudibleDistance;
    protected final T origin;

    protected TransitionState transitionState;
    protected boolean isFinished = false;
    protected int currentTick = 0, transitionTick = 0;
    protected float clampedDistance = 0.0f;


    public AbstractDynamicSoundInstance(T origin, SoundEvent soundEvent, SoundCategory soundCategory,
                                           float maxAudibleDistance,
                                           int startTransitionTicks, int endTransitionTicks) {
        super(soundEvent, soundCategory, SoundInstance.createRandom());
        this.origin = origin;
        this.repeat = true;
        this.volume = 0.0f;
        this.transitionState = TransitionState.STARTING;
        this.startTransitionTicks = startTransitionTicks;
        this.endTransitionTicks = endTransitionTicks;
        this.maxAudibleDistance = maxAudibleDistance;
        this.x = origin.getSoundPos().getX();
        this.y = origin.getSoundPos().getY();
        this.z = origin.getSoundPos().getZ();
    }

    @Override
    public void tick() {
        if (origin.getWorld().getTickManager().shouldTick()) this.currentTick++;
        else return;
        if (this.origin.stoppedExisting()) {
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

        this.x = origin.getSoundPos().x;
        this.y = origin.getSoundPos().y;
        this.z = origin.getSoundPos().z;

        if (isFinished()) {
            this.transitionState = TransitionState.STARTING;
            this.currentTick = 0;
            this.transitionTick = 0;
            this.setDone();
        }

        defaultSoundHandling(this);
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    public void finishSoundInstance() {
        this.transitionState = TransitionState.FINISHING;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    protected static void defaultSoundHandling(AbstractDynamicSoundInstance<? extends SoundOrigin> soundInstance) {
        boolean shouldTick = soundInstance.origin.getWorld().getTickManager().shouldTick();
        if (!shouldTick) {
            soundInstance.clampedDistance = 0.0f;
            soundInstance.volume = 0.0f;
            soundInstance.pitch = 1.0f;
        } else {
            soundInstance.transformSoundForDistance();
            soundInstance.transformSoundForMovement();
            soundInstance.transformSoundForTransition(0.0f, 1.0f, true, true);
        }
    }

    protected void transformSoundForMovement() {
        if (this.origin.getVelocity() == null) return;
        double horizontalVelocity = this.origin.getVelocity().horizontalLength();
        if (horizontalVelocity <= 0) this.volume = 0.0f;
        else this.pitch = (float) MathHelper.lerp(horizontalVelocity, 0.9f, 1.2f);
    }

    protected void transformSoundForDistance() {
        if (!(MinecraftClient.getInstance().player instanceof ClientPlayerEntity player)) return;
        this.clampedDistance = (float) Math.clamp(this.origin.getSoundPos().distanceTo(player.getPos()), 0.0, this.maxAudibleDistance);
        float normalizedIntensity = (this.maxAudibleDistance - this.clampedDistance) / this.maxAudibleDistance;
        this.volume = MathHelper.lerp(normalizedIntensity, 0.0f, 1.3f);
    }

    @SuppressWarnings("SameParameterValue")
    protected void transformSoundForTransition(float originalVolume, float originalPitch, boolean includeStarting, boolean includeStopping) {
        float normalizedStartTransitionTick = (float) this.transitionTick / this.startTransitionTicks;
        float normalizedEndTransitionTick = (float) this.transitionTick / this.endTransitionTicks;
        switch (this.transitionState) {
            case STARTING -> {
                if (includeStarting) {
                    this.volume = MathHelper.lerp(normalizedStartTransitionTick, 0.0f, originalVolume);
                    this.pitch = MathHelper.lerp(normalizedStartTransitionTick, originalPitch - 0.2f, originalPitch);
                }
            }
            case FINISHING -> {
                if (includeStopping) {
                    this.volume = MathHelper.lerp(normalizedEndTransitionTick, originalVolume, 0.0f);
                    this.pitch = MathHelper.lerp(normalizedEndTransitionTick, originalPitch, originalPitch - 0.2f);
                }
            }
        }
    }

    public enum TransitionState {STARTING, FINISHING, IDLE}
}
