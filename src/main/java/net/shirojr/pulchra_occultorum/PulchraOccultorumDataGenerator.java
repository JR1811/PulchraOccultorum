package net.shirojr.pulchra_occultorum;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.shirojr.pulchra_occultorum.datagen.LootTableProvider;
import net.shirojr.pulchra_occultorum.datagen.ModelProvider;
import net.shirojr.pulchra_occultorum.datagen.RecipeProvider;
import net.shirojr.pulchra_occultorum.datagen.TagProvider;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorumDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModelProvider::new);
        pack.addProvider(TagProvider.BlockTags::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(LootTableProvider::new);

        LoggerUtil.devLogger("Initialized datagen entrypoint");
    }
}
