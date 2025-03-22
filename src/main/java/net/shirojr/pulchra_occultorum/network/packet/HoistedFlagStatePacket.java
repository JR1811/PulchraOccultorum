package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.FlagPoleBlockEntity;

public record HoistedFlagStatePacket(float hoistedState, BlockPos pos) implements CustomPayload {

    public static final CustomPayload.Id<HoistedFlagStatePacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.getId("hoisted_state"));

    public static final PacketCodec<RegistryByteBuf, HoistedFlagStatePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, HoistedFlagStatePacket::hoistedState,
            BlockPos.PACKET_CODEC, HoistedFlagStatePacket::pos,
            HoistedFlagStatePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (!(context.player().getWorld() instanceof ServerWorld serverWorld)) return;
        if (!(serverWorld.getBlockEntity(this.pos()) instanceof FlagPoleBlockEntity blockEntity)) return;
        blockEntity.setHoistedState(this.hoistedState());
    }
}
