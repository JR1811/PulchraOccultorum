package net.shirojr.pulchra_occultorum.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.pulchra_occultorum.init.Blocks;

import java.util.concurrent.CompletableFuture;

public class LootTableProvider extends FabricBlockLootTableProvider {
    public LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(Blocks.ELASTIC_SAND);
        addDrop(Blocks.SAFETY_NET);
        addDrop(Blocks.SPOTLIGHT_LAMP);
        addDrop(Blocks.FLAG_POLE);
        addDrop(Blocks.FLAG_POLE_BASE);
        addDrop(Blocks.MONOLITH);
    }
}
