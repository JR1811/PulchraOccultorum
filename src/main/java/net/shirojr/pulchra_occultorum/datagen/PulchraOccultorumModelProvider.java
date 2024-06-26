package net.shirojr.pulchra_occultorum.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.init.Blocks;
import net.shirojr.pulchra_occultorum.init.Items;
import net.shirojr.pulchra_occultorum.util.BlockStateProperties;

import java.util.Locale;
import java.util.Optional;

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

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.FLAG_POLE)
                .coordinate(createFlagPoleBlockState()));

        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(Blocks.FLAG_POLE_BASE, PulchraOccultorum.identifierOf("block/flag_pole_base")));

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier
                .create(Blocks.MONOLITH)
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
                .coordinate(createDoubleBlockBlockState())
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (int i = 0; i <= 5; i++) {
            itemModelGenerator.register(Items.WHIP, String.format(Locale.ROOT, "_%02d", i), Models.GENERATED);
        }

        itemModelGenerator.register(Items.TAROT_WARLORD, Models.GENERATED);
        itemModelGenerator.register(Items.TAROT_MONOLITH, Models.GENERATED);
        itemModelGenerator.register(Blocks.SPOTLIGHT_LAMP.asItem(), new Model(Optional.of(
                PulchraOccultorum.identifierOf("block/spotlight_lamp_for_item")
        ), Optional.empty()));
    }

    private Identifier getIdentifier(String name) {
        return PulchraOccultorum.identifierOf(name);
    }

    private BlockStateVariantMap createFlagPoleBlockState() {
        return BlockStateVariantMap.create(BlockStateProperties.FLAG_POLE_STATE).register(flagPoleState -> {
            String path = "block/" + flagPoleState.asString();
            return BlockStateVariant.create().put(VariantSettings.MODEL, PulchraOccultorum.identifierOf(path));
        });
    }

    private BlockStateVariantMap createDoubleBlockBlockState() {
        return BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF).register(part ->
                switch (part) {
                    case UPPER ->
                            BlockStateVariant.create().put(VariantSettings.MODEL, PulchraOccultorum.identifierOf("block/monolith_top"));
                    case LOWER ->
                            BlockStateVariant.create().put(VariantSettings.MODEL, PulchraOccultorum.identifierOf("block/monolith_bottom"));
                }
        );
    }
}
