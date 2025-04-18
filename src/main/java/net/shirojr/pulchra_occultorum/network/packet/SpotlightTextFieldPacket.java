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
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.util.ShapeUtil;

import java.util.Optional;

public record SpotlightTextFieldPacket(BlockPos blockPos, Optional<Float> pitch, Optional<Float> yaw,
                                       Optional<Float> speed,
                                       Optional<Float> widthMultiplier) implements CustomPayload {

    public static final Id<SpotlightTextFieldPacket> IDENTIFIER =
            new Id<>(PulchraOccultorum.getId("spotlight_text_field_target_position"));

    public static final PacketCodec<RegistryByteBuf, SpotlightTextFieldPacket> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SpotlightTextFieldPacket::blockPos,
                    PacketCodecs.optional(PacketCodecs.FLOAT), SpotlightTextFieldPacket::pitch,
                    PacketCodecs.optional(PacketCodecs.FLOAT), SpotlightTextFieldPacket::yaw,
                    PacketCodecs.optional(PacketCodecs.FLOAT), SpotlightTextFieldPacket::speed,
                    PacketCodecs.optional(PacketCodecs.FLOAT), SpotlightTextFieldPacket::widthMultiplier,
                    SpotlightTextFieldPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (!(context.player().getWorld() instanceof ServerWorld world)) return;
        if (!(world.getBlockEntity(blockPos) instanceof SpotlightLampBlockEntity blockEntity)) return;
        if (yaw.isPresent() || pitch.isPresent()) {
            float finalYaw, finalPitch;

            if (pitch.isEmpty()) {
                finalPitch = blockEntity.getTargetRotation().getY();
            } else {
                finalPitch = pitch.orElse(0f);
            }

            if (yaw.isEmpty()) {
                finalYaw = blockEntity.getTargetRotation().getX();
            } else {
                finalYaw = yaw.orElse(0f);
            }

            blockEntity.setTargetRotation(() -> new ShapeUtil.Position(finalYaw, finalPitch));
        }
        speed.ifPresent(value -> blockEntity.setSpeed(Math.max(0, value)));
        widthMultiplier.ifPresent(value -> blockEntity.setWidthMultiplier(Math.max(0, value)));
    }
}
