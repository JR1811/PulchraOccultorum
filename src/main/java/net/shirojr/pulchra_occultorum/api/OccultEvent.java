package net.shirojr.pulchra_occultorum.api;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import net.shirojr.pulchra_occultorum.init.OccultEvents;

import java.util.List;

public interface OccultEvent {
    PacketCodec<RegistryByteBuf, OccultEvent> PACKET_CODEC = PacketCodecs.registryValue(CustomRegistries.OCCULT_EVENTS_REGISTRY_KEY);

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

