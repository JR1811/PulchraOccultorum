package net.shirojr.pulchra_occultorum.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

@SuppressWarnings("unused")
public class ItemGroups {
    public static final RegistryKey<ItemGroup> ITEMS = registerItemGroup("items", Items.STICK);
    public static final RegistryKey<ItemGroup> BLOCKS = registerItemGroup("blocks", Items.STICK);


    public static RegistryKey<ItemGroup> registerItemGroup(String name, Item icon) {
        Text displayName = Text.translatable("itemgroup.%s.%s".formatted(PulchraOccultorum.MOD_ID, name));
        ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(icon)).displayName(displayName).build();
        Registry.register(Registries.ITEM_GROUP, getRegistryKey(name), itemGroup);
        return getRegistryKey(name);
    }

    private static RegistryKey<ItemGroup> getRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, PulchraOccultorum.identifierOf(name));
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized item groups");
    }
}
