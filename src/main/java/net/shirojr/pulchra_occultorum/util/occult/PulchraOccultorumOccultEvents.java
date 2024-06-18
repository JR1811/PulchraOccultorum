package net.shirojr.pulchra_occultorum.util.occult;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

import java.util.Optional;

/**
 * Events, which can be predicted e.g. using Tarot cards
 */
public enum PulchraOccultorumOccultEvents implements OccultEvent {
    EMPTY("no_event"),
    HOSTILE_MOB_HORDE("hostile_mob_horde"),
    BETTER_LOOT_LUCK("better_loot_luck"),
    MONOLITH_SAFETY("monolith"),
    STRUCTURE_CHALLENGE("structure_challenge");

    private final Identifier identifier;

    PulchraOccultorumOccultEvents(String name) {
        this.identifier = PulchraOccultorum.identifierOf(name);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Optional<OccultEvent> registerOccultEvent(OccultEvent occultEvent) {
        if (CustomRegistries.OCCULT_EVENTS.stream().anyMatch(event -> event.getIdentifier().equals(occultEvent.getIdentifier()))) {
            LoggerUtil.LOGGER.error("Tried to register an occult event, which already exists. [ %s ]"
                    .formatted(occultEvent.getIdentifier().toString()));
            return Optional.empty();
        }
        OccultEvent registeredEvent = Registry.register(CustomRegistries.OCCULT_EVENTS, occultEvent.getIdentifier(), occultEvent);
        return Optional.of(registeredEvent);
    }

    private static void registerPulchraOccultorumOccultEvents() {
        for (OccultEvent event : PulchraOccultorumOccultEvents.values()) {
            registerOccultEvent(event);
        }
    }
}
