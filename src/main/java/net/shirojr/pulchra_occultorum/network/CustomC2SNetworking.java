package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.shirojr.pulchra_occultorum.network.packet.HoistedFlagStatePacket;
import net.shirojr.pulchra_occultorum.network.packet.HandlePositionPacket;
import net.shirojr.pulchra_occultorum.network.packet.SpotlightTextFieldPacket;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleMovementPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(UnicycleMovementPacket.IDENTIFIER, UnicycleMovementPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(HandlePositionPacket.IDENTIFIER, HandlePositionPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(SpotlightTextFieldPacket.IDENTIFIER, SpotlightTextFieldPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(HoistedFlagStatePacket.IDENTIFIER, HoistedFlagStatePacket::handlePacket);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom C2S networking");
    }
}
