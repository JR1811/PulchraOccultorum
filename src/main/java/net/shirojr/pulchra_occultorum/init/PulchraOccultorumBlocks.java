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
import net.shirojr.pulchra_occultorum.block.FlagPoleBaseBlock;
import net.shirojr.pulchra_occultorum.block.TestBlock;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class PulchraOccultorumBlocks {
    public static final TestBlock TEST_BLOCK_1 = register("test_block_1",
            new TestBlock(AbstractBlock.Settings.create()), null);
    public static final TestBlock TEST_BLOCK_2 =
            registerWithDefaultBlockItem("test_block_2", new TestBlock(AbstractBlock.Settings.create()));
    public static final Block FLAG_POLE_BASE =
            registerWithDefaultBlockItem("flag_pole_base", new FlagPoleBaseBlock(AbstractBlock.Settings.create().nonOpaque()));


    /**
     * Add Block instances to the Block Registry
     *
     * @param blockItemData if null, won't create a BlockItem for that Block
     */
    private static <T extends Block> T register(String name, T block, @Nullable BlockItemData blockItemData) {
        if (blockItemData != null) {
            BlockItem blockItem = new BlockItem(block, blockItemData.itemSettings());
            Registry.register(Registries.ITEM, PulchraOccultorum.identifierOf(name), blockItem);
            PulchraOccultorumItems.addToItemGroups(blockItem, blockItemData.itemGroups());
        }
        return Registry.register(Registries.BLOCK, PulchraOccultorum.identifierOf(name), block);
    }

    private static <T extends Block> T registerWithDefaultBlockItem(String name, T block) {
        return register(name, block, new BlockItemData(new Item.Settings(), List.of(PulchraOccultorumItemGroups.BLOCKS)));
    }

    private record BlockItemData(Item.Settings itemSettings, List<RegistryKey<ItemGroup>> itemGroups) {
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized blocks");
    }
}
