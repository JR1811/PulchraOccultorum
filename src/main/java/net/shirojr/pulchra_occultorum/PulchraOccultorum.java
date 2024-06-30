package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import net.shirojr.pulchra_occultorum.init.*;
import net.shirojr.pulchra_occultorum.network.CustomC2SNetworking;
import net.shirojr.pulchra_occultorum.network.NetworkPayloads;
import net.shirojr.pulchra_occultorum.init.SoundEvents;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorum implements ModInitializer {
    public static final String MOD_ID = "pulchra-occultorum";

    @Override
    public void onInitialize() {
        LoggerUtil.LOGGER.info("The Ringmaster appears...");

        Items.initialize();
        ItemGroups.initialize();
        CustomDataComponents.initialize();
        Blocks.initialize();
        BlockEntities.initialize();
        Entities.initialize();
        Tags.initialize();
        SoundEvents.initialize();
        ScreenHandlers.initialize();
        CustomRegistries.initialize();
        NetworkPayloads.initialize();
        CustomC2SNetworking.initialize();
        Events.initializeCommon();
        OccultEvents.initialize();

        LoggerUtil.devLogger("Initialized common entrypoint");
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of(MOD_ID, name);
    }
}