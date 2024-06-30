package net.shirojr.pulchra_occultorum.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.api.OccultEvent;
import net.shirojr.pulchra_occultorum.api.OccultEventCallback;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

import java.util.List;

public class CommonEvents {
    static {
        ServerTickEvents.START_SERVER_TICK.register(CommonEvents::handleTick);
        OccultEventCallback.EVENT.register(CommonEvents::handleOccultEvent);
    }

    public static void handleTick(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        long time = world.getTimeOfDay();
        var globalOccultEvents = CustomRegistries.OCCULT_EVENTS.stream().filter(occultEvent -> occultEvent.getActivationType().equals(OccultEvent.Type.GLOBAL));
        List<OccultEvent> activatedEvents = globalOccultEvents.filter(occultEvent -> occultEvent.startingTimes().contains(time)).toList();
        activatedEvents.forEach(occultEvent -> OccultEventCallback.EVENT.invoker().invoke(world, occultEvent));
    }

    public static void handleOccultEvent(World world, OccultEvent event) {
        if (world instanceof ServerWorld serverWorld) {
            event.onActivated(serverWorld);
        }
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized Server Events");
    }
}
