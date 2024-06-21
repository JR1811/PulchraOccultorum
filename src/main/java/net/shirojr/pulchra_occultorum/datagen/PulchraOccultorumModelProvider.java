package net.shirojr.pulchra_occultorum.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.init.Blocks;
import net.shirojr.pulchra_occultorum.init.Items;

import java.util.Locale;

public class PulchraOccultorumModelProvider extends FabricModelProvider {
    public PulchraOccultorumModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(Blocks.SAFETY_NET, getIdentifier("block/safety_net")));
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(Blocks.ELASTIC_SAND, getIdentifier("block/elastic_sand")));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (int i = 0; i <= 4; i++) {
            itemModelGenerator.register(Items.WHIP, String.format(Locale.ROOT, "_%02d", i), Models.GENERATED);
        }
    }

    private Identifier getIdentifier(String name) {
        return PulchraOccultorum.identifierOf(name);
    }
}
