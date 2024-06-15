package net.shirojr.pulchra_occultorum.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomDataComponents {
    public static final ComponentType<Integer> TICK = register("tick",
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).build());

    private static <T> ComponentType<T> register(String name, ComponentType<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, PulchraOccultorum.identifierOf(name), builder);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom data components for Items");
    }
}
