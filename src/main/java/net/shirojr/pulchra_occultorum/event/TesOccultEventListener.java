package net.shirojr.pulchra_occultorum.event;

import net.shirojr.pulchra_occultorum.api.OccultEventCallback;

public class TesOccultEventListener {
    static {
        OccultEventCallback.EVENT.register((serverWorld, event) -> {
            event.onActivated(serverWorld);
        });
    }
}
