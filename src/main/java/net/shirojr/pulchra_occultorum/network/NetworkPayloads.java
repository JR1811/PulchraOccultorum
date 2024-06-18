package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleSoundPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

@SuppressWarnings("SameParameterValue")
public class NetworkPayloads {
    static {
        registerS2C(UnicycleSoundPacket.IDENTIFIER, UnicycleSoundPacket.CODEC);
    }

    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetIdentifier, codec);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized payload registering for custom networking");
    }
}
