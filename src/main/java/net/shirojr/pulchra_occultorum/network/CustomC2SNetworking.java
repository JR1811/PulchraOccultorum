package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.network.packet.PositionPacket;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleMovementPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(UnicycleMovementPacket.IDENTIFIER, CustomC2SNetworking::handleUnicycleMovement);
        ServerPlayNetworking.registerGlobalReceiver(PositionPacket.IDENTIFIER, PositionPacket::handlePositionPacket);
    }

    private static void handleUnicycleMovement(UnicycleMovementPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (!(player.getVehicle() instanceof UnicycleEntity unicycleEntity)) return;
        UnicycleEntity.DirectionInput[] input = new UnicycleEntity.DirectionInput[] {null, null, null};

        if (payload.inputLeft() != null) input[0] = payload.inputLeft();
        if (payload.inputRight() != null) input[1] = payload.inputRight();
        if (payload.inputJump() != null) input[2] = payload.inputJump();
        unicycleEntity.setDirectionInputs(input);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom C2S networking");
    }
}
