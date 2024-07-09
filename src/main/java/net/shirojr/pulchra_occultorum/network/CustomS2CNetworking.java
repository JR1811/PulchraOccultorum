package net.shirojr.pulchra_occultorum.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleSoundPacket;
import net.shirojr.pulchra_occultorum.sound.SoundManager;
import net.shirojr.pulchra_occultorum.sound.instance.UnicycleRollSoundInstance;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class CustomS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(UnicycleSoundPacket.IDENTIFIER, UnicycleSoundPacket::handlePacket);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom S2C networking");
    }
}
