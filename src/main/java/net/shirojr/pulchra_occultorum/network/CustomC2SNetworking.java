package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomC2SNetworking {


    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetIdentifier, codec);
    }


    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom C2S networking");
    }
}
