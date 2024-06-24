package net.shirojr.pulchra_occultorum.api;

import net.minecraft.server.world.ServerWorld;

import java.util.List;

public interface OccultEvent {
    default Type getActivationType() {
        return Type.GLOBAL;
    }

    List<Long> startingTimes();

    default Long getDuration() {
        return 0L;
    }

    void onActivated(ServerWorld serverWorld);

    default boolean isRunning(long currentTime) {
        for (Long entry : startingTimes()) {
            if (entry <= currentTime && entry < getDuration()) return true;
        }
        return false;
    }

    enum Type {
        GLOBAL, PLAYER_SPECIFIC;
    }
}

