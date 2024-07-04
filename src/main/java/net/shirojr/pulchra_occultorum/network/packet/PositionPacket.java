package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.util.ShapeUtil;

import java.util.Optional;

public record PositionPacket(String name, BlockPos blockPos, Optional<Float> normalizedX,
                             Optional<Float> normalizedY) implements CustomPayload {

    public static final CustomPayload.Id<PositionPacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.identifierOf("position"));

    public static final PacketCodec<RegistryByteBuf, PositionPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, PositionPacket::name,
                    BlockPos.PACKET_CODEC, PositionPacket::blockPos,
                    PacketCodecs.optional(PacketCodecs.FLOAT), PositionPacket::normalizedX,
                    PacketCodecs.optional(PacketCodecs.FLOAT), PositionPacket::normalizedY,
                    PositionPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void handlePositionPacket(ServerPlayNetworking.Context context) {
        if (!(context.player().getWorld() instanceof ServerWorld world)) return;
        if (!(world.getBlockEntity(this.blockPos()) instanceof SpotlightLampBlockEntity blockEntity)) return;
        if (this.name().equals("big_handle")) {
            float lerpedX = MathHelper.lerp(this.normalizedX().orElse(0f), -SpotlightLampBlockEntity.MAX_YAW_RANGE, SpotlightLampBlockEntity.MAX_YAW_RANGE);
            float lerpedY = MathHelper.lerp(this.normalizedY().orElse(0f), -SpotlightLampBlockEntity.MAX_PITCH_RANGE, SpotlightLampBlockEntity.MAX_PITCH_RANGE);
            blockEntity.syncedTargetRotationModification(() -> new ShapeUtil.Position(lerpedX, lerpedY));
        }
        if (this.name.equals("small_handle")) {
            float lerpedY = MathHelper.lerp(this.normalizedY().orElse(0f), 0f, SpotlightLampBlockEntity.MAX_TURNING_SPEED);
            blockEntity.syncedSpeedModification(() -> lerpedY);
        }
    }
}
