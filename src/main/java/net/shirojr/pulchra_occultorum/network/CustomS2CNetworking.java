package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.pulchra_occultorum.network.packet.MobEntitySyncPacket;
import net.shirojr.pulchra_occultorum.network.packet.SpotlightSoundPacket;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleSoundPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(UnicycleSoundPacket.IDENTIFIER, UnicycleSoundPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(SpotlightSoundPacket.IDENTIFIER, SpotlightSoundPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(MobEntitySyncPacket.IDENTIFIER, MobEntitySyncPacket::handlePacket);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom S2C networking");
    }
}
