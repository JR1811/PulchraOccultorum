package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.ModelPredicateProviders;

public class PulchraOccultorumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateProviders.initialize();

        LoggerUtil.devLogger("Initialized client entrypoint");
    }
}
