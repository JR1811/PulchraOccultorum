package net.shirojr.pulchra_occultorum.init;

import com.google.common.base.Supplier;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

@SuppressWarnings("Convert2MethodRef")
public class ItemGroups {
    public static final RegistryKey<ItemGroup> ITEMS = registerItemGroup("items", () -> Items.TAROT_MONOLITH);
    public static final RegistryKey<ItemGroup> BLOCKS = registerItemGroup("blocks", () -> Blocks.SPOTLIGHT_LAMP.asItem());


    public static RegistryKey<ItemGroup> registerItemGroup(String name, Supplier<Item> icon) {
        Text displayName = Text.translatable("itemgroup.%s.%s".formatted(PulchraOccultorum.MOD_ID, name));
        ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(icon.get())).displayName(displayName).build();
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
