package net.shirojr.pulchra_occultorum.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.*;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class Blocks {
    public static final FlagPoleBaseBlock FLAG_POLE_BASE =
            register("flag_pole_base",
                    new FlagPoleBaseBlock(AbstractBlock.Settings.create()),
                    new BlockItemData(new Item.Settings(),
                            List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.REDSTONE)));

    public static final FlagPoleBlock FLAG_POLE = register("flag_pole",
            new FlagPoleBlock(AbstractBlock.Settings.create().nonOpaque()),
            new BlockItemData(new Item.Settings(),
                    List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.FUNCTIONAL)));

    public static final SpotlightLampBlock SPOTLIGHT_LAMP =
            register("spotlight_lamp",
                    new SpotlightLampBlock(AbstractBlock.Settings.create()
                            .nonOpaque().luminance(SpotlightLampBlock::luminanceFromBlockState)),
                    new BlockItemData(new Item.Settings(),
                            List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.FUNCTIONAL)));

    public static final ElasticSandBlock ELASTIC_SAND = register("elastic_sand",
            new ElasticSandBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.SAND)),
            new BlockItemData(new Item.Settings(),
                    List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.FUNCTIONAL)));

    public static final SafetyNetBlock SAFETY_NET = register("safety_net",
            new SafetyNetBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.WHITE_WOOL).nonOpaque()),
            new BlockItemData(new Item.Settings(),
                    List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.FUNCTIONAL)));

    public static final MonolithBlock MONOLITH = register("monolith",
            new MonolithBlock(AbstractBlock.Settings.create().nonOpaque()),
            new BlockItemData(new Item.Settings(),
                    List.of(ItemGroups.BLOCKS, net.minecraft.item.ItemGroups.FUNCTIONAL)));


    private static <T extends Block> T register(String name, T block, @Nullable BlockItemData blockItemData) {
        if (blockItemData != null) {
            BlockItem blockItem = new BlockItem(block, blockItemData.itemSettings());
            Registry.register(Registries.ITEM, PulchraOccultorum.getId(name), blockItem);
            Items.addToItemGroups(blockItem, blockItemData.itemGroups());
        }
        return Registry.register(Registries.BLOCK, PulchraOccultorum.getId(name), block);
    }

    private static <T extends Block> T registerWithDefaultBlockItem(String name, T block) {
        return register(name, block, new BlockItemData(new Item.Settings(), List.of(ItemGroups.BLOCKS)));
    }

    private record BlockItemData(Item.Settings itemSettings, List<RegistryKey<ItemGroup>> itemGroups) {
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized blocks");
    }
}
