package net.shirojr.pulchra_occultorum.init;

import net.shirojr.pulchra_occultorum.event.ClientEvents;
import net.shirojr.pulchra_occultorum.event.CommonEvents;

public class Events {
    public static void initializeCommon() {
        CommonEvents.initialize();
    }

    public static void registerClient() {
        ClientEvents.initialize();
    }
}
