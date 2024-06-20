package net.shirojr.pulchra_occultorum.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.shirojr.pulchra_occultorum.api.OccultEventCallback;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;

public class ServerEvents {
    static {
        ServerTickEvents.START_SERVER_TICK.register(ServerEvents::test);

    }

    public static void test(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        long time = world.getTimeOfDay();
        CustomRegistries.OCCULT_EVENTS.stream().filter(occultEvent -> occultEvent.getStartTime().contains(time))
                .forEach(occultEvent -> OccultEventCallback.EVENT.invoker().invoke(occultEvent));
    }

    //TODO: block place / destroy
    //      get nearby blocks and cache the region(s)
    //      check if they are the glass
    //      heat up BlockPos using persistent data?
}
