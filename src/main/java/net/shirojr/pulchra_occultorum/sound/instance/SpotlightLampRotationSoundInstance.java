package net.shirojr.pulchra_occultorum.sound.instance;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractDynamicSoundInstance;

public class SpotlightLampRotationSoundInstance extends AbstractDynamicSoundInstance<SpotlightLampBlockEntity> {
    public static final float MAX_SOUND_DISTANCE = 30;

    public SpotlightLampRotationSoundInstance(SpotlightLampBlockEntity entity) {
        super(entity, SoundEvents.SPOTLIGHT_LAMP_MOVE, SoundCategory.NEUTRAL, MAX_SOUND_DISTANCE, 30, 30);
    }

    @Override
    protected void transformSoundForTransition(float originalVolume, float originalPitch, boolean includeStarting, boolean includeStopping) {
        float normalizedStartTransitionTick = (float) this.transitionTick / this.startTransitionTicks;
        float normalizedEndTransitionTick = (float) this.transitionTick / this.endTransitionTicks;
        switch (this.transitionState) {
            case STARTING -> {
                if (includeStarting) {
                    // this.volume = MathHelper.lerp(normalizedStartTransitionTick, 0.0f, originalVolume);
                    this.pitch = MathHelper.lerp(normalizedStartTransitionTick, originalPitch - 0.5f, originalPitch);
                }
            }
            case FINISHING -> {
                if (includeStopping) {
                    this.volume = MathHelper.lerp(normalizedEndTransitionTick, 0.7f, originalVolume);
                    this.pitch = MathHelper.lerp(normalizedEndTransitionTick, originalPitch, originalPitch - 0.6f);
                }
            }
        }
    }
}
