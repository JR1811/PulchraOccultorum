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
        ClientPlayNetworking.registerGlobalReceiver(UnicycleSoundPacket.IDENTIFIER, CustomS2CNetworking::handleUnicyclePacket);
    }

    private static void handleUnicyclePacket(UnicycleSoundPacket packet, ClientPlayNetworking.Context context) {
        ClientWorld world = context.client().world;
        int entityNetworkId = packet.entityNetworkId();
        boolean shouldPlay = packet.shouldPlay();
        if (world == null) return;
        if (!(world.getEntityById(entityNetworkId) instanceof UnicycleEntity unicycleEntity)) return;

        LoggerUtil.devLogger("world: %s | uuid: %s | isLoaded %s".formatted(world, entityNetworkId, shouldPlay));
        SoundManager soundManager = SoundManager.getInstance();
        if (shouldPlay) soundManager.play(unicycleEntity, new UnicycleRollSoundInstance(unicycleEntity, 60, 60));
        else soundManager.stopAll(unicycleEntity);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized custom S2C networking");
    }
}
