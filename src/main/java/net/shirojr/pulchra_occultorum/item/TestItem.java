package net.shirojr.pulchra_occultorum.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.pulchra_occultorum.persistent.PersistentStateHandler;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.occult.PulchraOccultorumOccultEvents;
import net.shirojr.pulchra_occultorum.util.data.OccultEventsPlayerData;

import java.util.List;

public class TestItem extends Item {
    public TestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            OccultEventsPlayerData playerData = PersistentStateHandler.getPlayerEventData(serverPlayer);
            if (user.isSneaking() && playerData != null) {
                PulchraOccultorumOccultEvents lootEvent = PulchraOccultorumOccultEvents.BETTER_LOOT_LUCK;
                PulchraOccultorumOccultEvents mobEvent = PulchraOccultorumOccultEvents.HOSTILE_MOB_HORDE;
                playerData.addOccultEvents(List.of(lootEvent));
                playerData.addOccultEvents(List.of(mobEvent));

                playerData.getPastOccultEventsList().forEach(event -> LoggerUtil.devLogger("OccultEvent: " + event.getIdentifier().toString()));
            }
        }
        return super.use(world, user, hand);
    }
}
