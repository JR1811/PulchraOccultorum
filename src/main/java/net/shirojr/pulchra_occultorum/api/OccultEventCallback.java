package net.shirojr.pulchra_occultorum.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public interface OccultEventCallback {
    void invoke(ServerWorld world, OccultEvent event);
    Event<OccultEventCallback> EVENT = EventFactory.createArrayBacked(OccultEventCallback.class, (listeners) -> (world, occultEvent) ->  {
        for (OccultEventCallback listener : listeners) {
            listener.invoke(world, occultEvent);
        }
    });
}
