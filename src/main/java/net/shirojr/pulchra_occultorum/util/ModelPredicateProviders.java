package net.shirojr.pulchra_occultorum.util;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.init.Items;
import net.shirojr.pulchra_occultorum.util.boilerplate.AbstractTickingItem;

@SuppressWarnings("SameParameterValue")
public class ModelPredicateProviders {
    static {
        registerLinearAnimation(Items.WHIP);
    }

    private static void register(Item item, String name, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla(name), provider);
    }

    private static void registerLinearAnimation(Item item) {
        register(item, "animation", (stack, world, entity, seed) -> {
            if (!(stack.getItem() instanceof AbstractTickingItem tickingItem)) return 0.0f;
            if (!tickingItem.hasMaxTick(stack)) return 0.0f;
            return (float) tickingItem.getTick(stack) / tickingItem.getMaxTick(stack);
        });
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized ModelPredicateProviders");
    }
}
