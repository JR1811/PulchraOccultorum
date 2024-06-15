package net.shirojr.pulchra_occultorum.api;

import net.minecraft.util.Identifier;

import java.util.List;

public interface OccultEvent {
    Identifier getIdentifier();

    default List<Long> getStartTime() {
        return List.of(-1L);
    }

    default Long getDuration() {
        return 0L;
    }

    // default boolean
}

