package net.shirojr.pulchra_occultorum.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantmentUtil {
    public static boolean hasEnchantment(ItemStack stack, RegistryKey<Enchantment> enchantment) {
        ItemEnchantmentsComponent enchantmentsComponent = EnchantmentHelper.getEnchantments(stack);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantmentsComponent.getEnchantmentEntries()) {
            if (enchantment.equals(entry.getKey())) return true;
        }
        return false;
    }
}
