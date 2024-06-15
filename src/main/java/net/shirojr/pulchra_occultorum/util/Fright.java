package net.shirojr.pulchra_occultorum.util;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface Fright {
    int MAX_FRIGHTENED_TICKS = 100;

    int pulchraOccultorum$getFrightenedTicksLeft();

    void pulchraOccultorum$setFrightenedTicksLeft(int ticks);

    boolean pulchraOccultorum$isFrightened();

    void pulchraOccultorum$setAggressor(LivingEntity aggressorEntity);

    @Nullable
    LivingEntity pulchraOccultorum$getAggressor();
}
