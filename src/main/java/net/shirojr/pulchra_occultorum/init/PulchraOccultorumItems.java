package net.shirojr.pulchra_occultorum.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.item.TestItem;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class PulchraOccultorumItems {
    public static final TestItem TEST_ITEM_1 = register("test_item_1", new TestItem(new Item.Settings()),
            null);
    public static final TestItem TEST_ITEM_2 = register("test_item_2", new TestItem(new Item.Settings()),
            List.of(ItemGroups.COMBAT, PulchraOccultorumItemGroups.ITEMS));


    private static <T extends Item> T register(String name, T item, @Nullable List<RegistryKey<ItemGroup>> itemGroups) {
        Registry.register(Registries.ITEM, PulchraOccultorum.identifierOf(name), item);
        if (itemGroups != null) {
            addToItemGroups(item, itemGroups);
        }
        return item;
    }

    public static void addToItemGroups(Item item, List<RegistryKey<ItemGroup>> itemGroups) {
        for (var group : itemGroups) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(new ItemStack(item)));
        }
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized items");
    }
}
