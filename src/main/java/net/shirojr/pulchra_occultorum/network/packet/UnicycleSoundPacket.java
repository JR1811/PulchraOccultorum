package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.sound.SoundManager;
import net.shirojr.pulchra_occultorum.sound.instance.UnicycleRollSoundInstance;

public record UnicycleSoundPacket(int entityNetworkId, boolean shouldPlay) implements CustomPayload {

    public static final CustomPayload.Id<UnicycleSoundPacket> IDENTIFIER = new CustomPayload.Id<>(PulchraOccultorum.identifierOf("unicycle_rolling"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static final PacketCodec<RegistryByteBuf, UnicycleSoundPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, UnicycleSoundPacket::entityNetworkId,
            PacketCodecs.BOOL, UnicycleSoundPacket::shouldPlay,
            UnicycleSoundPacket::new
    );

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientWorld world = context.client().world;
        int entityNetworkId = this.entityNetworkId();
        boolean shouldPlay = this.shouldPlay();
        if (world == null) return;
        if (!(world.getEntityById(entityNetworkId) instanceof UnicycleEntity unicycleEntity)) return;
        SoundManager soundManager = SoundManager.getInstance();
        if (shouldPlay) soundManager.play(unicycleEntity, new UnicycleRollSoundInstance(unicycleEntity, 60, 60));
        else soundManager.stopAll(unicycleEntity);
    }
}
