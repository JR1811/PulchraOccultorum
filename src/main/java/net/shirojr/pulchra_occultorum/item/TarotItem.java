package net.shirojr.pulchra_occultorum.item;

import net.minecraft.item.Item;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.init.OccultEvents;

import java.util.List;

public class TarotItem extends Item {
    private final List<OccultEvent> connectedEvents;

    public TarotItem(Settings settings, List<OccultEvent> connectedEvents) {
        super(settings);
        this.connectedEvents = connectedEvents;
    }

    public List<OccultEvent> getConnectedEvents() {
        return this.connectedEvents;
    }
}
