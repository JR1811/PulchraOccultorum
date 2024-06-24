package net.shirojr.pulchra_occultorum.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BasicOccultEvent(List<Long> startingTimes) implements OccultEvent {

    @Nullable
    public Identifier getIdentifier() {
        return CustomRegistries.OCCULT_EVENTS.getId(this);
    }

    @Override
    public void onActivated(ServerWorld serverWorld) {
        LoggerUtil.devLogger("activated %s occult event".formatted(getIdentifier()));
    }
}
