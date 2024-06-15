package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.init.Registries;
import net.shirojr.pulchra_occultorum.init.*;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorum implements ModInitializer {
    public static final String MOD_ID = "pulchra-occultorum";

    @Override
    public void onInitialize() {
        LoggerUtil.LOGGER.info("The Ringmaster appears...");

        Items.initialize();
        ItemGroups.initialize();
        Blocks.initialize();
        CustomDataComponents.initialize();
        BlockEntities.initialize();
        Entities.initialize();
        Registries.initialize();

        LoggerUtil.devLogger("Initialized common entrypoint");
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of(MOD_ID, name);
    }
}