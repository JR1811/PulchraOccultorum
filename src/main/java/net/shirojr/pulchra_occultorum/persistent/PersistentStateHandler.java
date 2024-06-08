package net.shirojr.pulchra_occultorum.persistent;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.NbtKeys;
import net.shirojr.pulchra_occultorum.util.data.OccultEventsPlayerData;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class to access and manipulate persistent data.
 * Currently, following things are saved, can be changed and are accessible:
 * <ul>
 *     <li>Occult Events</li>
 * </ul>
 *
 * <h3>Occult Events</h3>
 * Keeps track of events, which can be predicted using occult game mechanics. Multiple events are stored on a player's
 * UUID
 */
public class PersistentStateHandler extends PersistentState {
    private final HashMap<UUID, OccultEventsPlayerData> playerEvents = new HashMap<>();

    public HashMap<UUID, OccultEventsPlayerData> getPlayerEventsList() {
        return this.playerEvents;
    }

    public static OccultEventsPlayerData getPlayerEventData(ServerPlayerEntity player) {
        if (player.getWorld() == null || player.getWorld().getServer() == null) return null;
        PersistentStateHandler handler = getServerState(player.getWorld().getServer());
        return handler.playerEvents.computeIfAbsent(player.getUuid(), uuid -> new OccultEventsPlayerData(null));
    }

    public static PersistentStateHandler getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        Type<PersistentStateHandler> type = new Type<>(
                PersistentStateHandler::new, PersistentStateHandler::createFromNbt, DataFixTypes.PLAYER);
        PersistentStateHandler handler = manager.getOrCreate(type, PulchraOccultorum.MOD_ID);
        handler.markDirty();
        return handler;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound eventCompound = new NbtCompound();
        for (var entry : this.playerEvents.entrySet()) {
            eventCompound.put(entry.getKey().toString(), OccultEventsPlayerData.toNbt(entry.getValue()));
        }
        nbt.put(NbtKeys.OCCULT_EVENTS, eventCompound);
        return nbt;
    }

    public static PersistentStateHandler createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        PersistentStateHandler handler = new PersistentStateHandler();
        NbtCompound eventCompound = nbt.getCompound(NbtKeys.OCCULT_EVENTS);
        for (String uuid : eventCompound.getKeys()) {
            NbtCompound playerCompound = eventCompound.getCompound(uuid);
            handler.playerEvents.put(UUID.fromString(uuid), OccultEventsPlayerData.fromNbt(playerCompound));
        }
        return handler;
    }
}
