package net.shirojr.pulchra_occultorum;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.shirojr.pulchra_occultorum.datagen.PulchraOccultorumModelProvider;
import net.shirojr.pulchra_occultorum.datagen.PulchraOccultorumTagProvider;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorumDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(PulchraOccultorumModelProvider::new);
        pack.addProvider(PulchraOccultorumTagProvider.BlockTags::new);

        LoggerUtil.devLogger("Initialized datagen entrypoint");
    }
}
