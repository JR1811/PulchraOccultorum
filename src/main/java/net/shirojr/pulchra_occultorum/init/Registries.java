package net.shirojr.pulchra_occultorum.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.*;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

@SuppressWarnings("SameParameterValue")
public class Registries {
    public static final RegistryKey<Registry<OccultEvent>> OCCULT_EVENTS_REGISTRY_KEY =
            RegistryKey.ofRegistry(PulchraOccultorum.identifierOf("occult_events"));

    public static final SimpleRegistry<OccultEvent> OCCULT_EVENTS = registerRegistry(OCCULT_EVENTS_REGISTRY_KEY);

    private static <T> SimpleRegistry<T> registerRegistry(RegistryKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }



    public static void initialize() {
        LoggerUtil.devLogger("Initialized registry of occult event entries");
    }
}
