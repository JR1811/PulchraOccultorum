package net.shirojr.pulchra_occultorum.init;

import net.minecraft.registry.Registry;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.api.BasicOccultEvent;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.api.OccultEventUtil;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

import java.util.List;

public class OccultEvents {
    public static final OccultEvent EMPTY = register("no_event",
            new BasicOccultEvent(List.of()));

    public static final OccultEvent HOSTILE_HORDE = register("hostile_mob_horde",
            new BasicOccultEvent(List.of(OccultEventUtil.timeFromDays(4))));

    public static final OccultEvent LOOT_LUCK = register("better_loot_luck",
            new BasicOccultEvent(List.of()));

    public static final OccultEvent MONOLITH = register("monolith",
            new BasicOccultEvent(List.of()));

    public static final OccultEvent STRUCTURE_CHALLENGE = register("structure_challenge",
            new BasicOccultEvent(List.of()));


    private static <T extends OccultEvent> T register(String name, T occultEvent) {
        return Registry.register(CustomRegistries.OCCULT_EVENTS, PulchraOccultorum.identifierOf(name), occultEvent);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized Occult Events");
    }
}
