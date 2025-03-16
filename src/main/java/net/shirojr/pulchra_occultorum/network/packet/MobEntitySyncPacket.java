package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.Fright;

import java.util.Collection;

public record MobEntitySyncPacket(int entityId, int frightenedTicks) implements CustomPayload {
    public static final CustomPayload.Id<MobEntitySyncPacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.identifierOf("mob_entity_sync"));

    public static final PacketCodec<RegistryByteBuf, MobEntitySyncPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, MobEntitySyncPacket::entityId,
            PacketCodecs.INTEGER, MobEntitySyncPacket::frightenedTicks,
            MobEntitySyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(Collection<ServerPlayerEntity> targets) {
        targets.forEach(target -> ServerPlayNetworking.send(target, this));
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        if (world == null || !(world.getEntityById(entityId) instanceof Fright frightable)) return;
        frightable.pulchraOccultorum$setFrightenedTicksLeft(frightenedTicks);
    }
}
