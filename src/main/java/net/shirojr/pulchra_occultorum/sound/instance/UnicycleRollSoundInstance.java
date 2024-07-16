package net.shirojr.pulchra_occultorum.sound.instance;

import net.minecraft.sound.SoundCategory;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractDynamicSoundInstance;
import net.shirojr.pulchra_occultorum.util.SoundOrigin;

public class UnicycleRollSoundInstance extends AbstractDynamicSoundInstance<SoundOrigin> {
    public UnicycleRollSoundInstance(SoundOrigin entity) {
        super(entity, SoundEvents.UNICYCLE_ROLL, SoundCategory.NEUTRAL, 20, 60, 60);
    }
}
