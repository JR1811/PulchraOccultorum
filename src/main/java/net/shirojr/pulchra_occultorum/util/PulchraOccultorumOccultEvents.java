package net.shirojr.pulchra_occultorum.util;

import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Events, which can be predicted e.g. using Tarot cards
 */
public enum PulchraOccultorumOccultEvents implements OccultEvent {
    HOSTILE_MOB_HORDE(PulchraOccultorum.identifierOf("hostile_mob_horde")),
    BETTER_LOOT_LUCK(PulchraOccultorum.identifierOf("better_loot_luck"));

    private final Identifier identifier;

    PulchraOccultorumOccultEvents(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Nullable
    public static PulchraOccultorumOccultEvents fromIdentifier(Identifier occultEvent) {
        // TODO: add API compat. Other mods should be able to add to the list as well.
        //      This enum values list, which is used here, is only temporary!
        for (PulchraOccultorumOccultEvents event : PulchraOccultorumOccultEvents.values()) {
            if (event.getIdentifier().equals(occultEvent)) return event;
        }
        return null;
    }
}
