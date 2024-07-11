package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.shirojr.pulchra_occultorum.network.packet.HoistedFlagStatePacket;
import net.shirojr.pulchra_occultorum.network.packet.PositionPacket;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleMovementPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(UnicycleMovementPacket.IDENTIFIER, UnicycleMovementPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(PositionPacket.IDENTIFIER, PositionPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(HoistedFlagStatePacket.IDENTIFIER, HoistedFlagStatePacket::handlePacket);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom C2S networking");
    }
}
