package net.shirojr.pulchra_occultorum.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface OccultEventCallback {
    void invoke(OccultEvent event);

    Event<OccultEventCallback> EVENT = EventFactory.createArrayBacked(OccultEventCallback.class, (listeners) -> (occultEvent) ->  {
        for (OccultEventCallback listener : listeners) {
            listener.invoke(occultEvent);
        }
    });
}
