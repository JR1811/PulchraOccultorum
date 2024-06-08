package net.shirojr.pulchra_occultorum.util.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import net.shirojr.pulchra_occultorum.util.PulchraOccultorumOccultEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OccultEventsPlayerData {
    private final List<PulchraOccultorumOccultEvents> pastOccultEventsList = new ArrayList<>();

    public OccultEventsPlayerData(@Nullable List<PulchraOccultorumOccultEvents> occultEventsList) {
        if (occultEventsList != null) {
            this.pastOccultEventsList.addAll(occultEventsList);
        }
    }

    public List<PulchraOccultorumOccultEvents> getPastOccultEventsList() {
        return pastOccultEventsList;
    }

    public void addOccultEvents(List<PulchraOccultorumOccultEvents> occultEvents) {
        this.pastOccultEventsList.addAll(occultEvents);
    }

    public void addOccultEvents(int index, List<PulchraOccultorumOccultEvents> occultEvents) {
        this.pastOccultEventsList.addAll(index, occultEvents);
    }

    public void removeOccultEvent(int index) {
        this.pastOccultEventsList.remove(index);
    }

    public static OccultEventsPlayerData fromNbt(NbtCompound nbt) {
        OccultEventsPlayerData playerData = new OccultEventsPlayerData(null);
        List<PulchraOccultorumOccultEvents> events = new ArrayList<>();

        int eventListSize = nbt.getInt(NbtKeys.OCCULT_EVENTS_LIST_SIZE);
        for (int index = 0; index < eventListSize; index++) {
            String id = nbt.getString(String.valueOf(index));
            events.add(PulchraOccultorumOccultEvents.fromIdentifier(Identifier.of(id)));
        }
        playerData.addOccultEvents(events);
        return playerData;
    }

    public static NbtCompound toNbt(OccultEventsPlayerData playerData) {
        NbtCompound eventsNbt = new NbtCompound();
        eventsNbt.putInt(NbtKeys.OCCULT_EVENTS_LIST_SIZE, playerData.getPastOccultEventsList().size());
        for (int index = 0; index < playerData.getPastOccultEventsList().size(); index++) {
            PulchraOccultorumOccultEvents event = playerData.getPastOccultEventsList().get(index);
            eventsNbt.putString(String.valueOf(index), event.getIdentifier().toString());
        }
        return eventsNbt;
    }
}
