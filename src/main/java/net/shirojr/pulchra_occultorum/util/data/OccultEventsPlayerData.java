package net.shirojr.pulchra_occultorum.util.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.api.OccultEventUtil;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import net.shirojr.pulchra_occultorum.util.occult.PulchraOccultorumOccultEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OccultEventsPlayerData {
    private final List<OccultEvent> pastOccultEventsList = new ArrayList<>();

    public OccultEventsPlayerData(@Nullable List<PulchraOccultorumOccultEvents> occultEventsList) {
        if (occultEventsList != null) {
            this.pastOccultEventsList.addAll(occultEventsList);
        }
    }

    public List<OccultEvent> getPastOccultEventsList() {
        return pastOccultEventsList;
    }

    public void addOccultEvents(List<OccultEvent> occultEvents) {
        this.pastOccultEventsList.addAll(occultEvents);
    }

    public void addOccultEvents(int index, List<OccultEvent> occultEvents) {
        this.pastOccultEventsList.addAll(index, occultEvents);
    }

    public void removeOccultEvent(int index) {
        this.pastOccultEventsList.remove(index);
    }

    public static OccultEventsPlayerData fromNbt(NbtCompound nbt) {
        OccultEventsPlayerData playerData = new OccultEventsPlayerData(null);
        List<OccultEvent> events = new ArrayList<>();

        int eventListSize = nbt.getInt(NbtKeys.OCCULT_EVENTS_LIST_SIZE);
        for (int index = 0; index < eventListSize; index++) {
            String id = nbt.getString(String.valueOf(index));
            events.add(OccultEventUtil.fromIdentifier(Identifier.of(id)));
        }
        playerData.addOccultEvents(events);
        return playerData;
    }

    public static NbtCompound toNbt(OccultEventsPlayerData playerData) {
        NbtCompound eventsNbt = new NbtCompound();
        eventsNbt.putInt(NbtKeys.OCCULT_EVENTS_LIST_SIZE, playerData.getPastOccultEventsList().size());
        for (int index = 0; index < playerData.getPastOccultEventsList().size(); index++) {
            OccultEvent event = playerData.getPastOccultEventsList().get(index);
            eventsNbt.putString(String.valueOf(index), event.getIdentifier().toString());
        }
        return eventsNbt;
    }
}
