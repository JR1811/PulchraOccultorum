package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        LoggerUtil.devLogger("Initialized client entrypoint");
    }
}
