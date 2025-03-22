package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

public record HandlePositionPacket(String name, BlockPos blockPos, Optional<Float> normalizedX,
                                   Optional<Float> normalizedY) implements CustomPayload {

    public static final CustomPayload.Id<HandlePositionPacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.getId("position"));

    public static final PacketCodec<RegistryByteBuf, HandlePositionPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, HandlePositionPacket::name,
                    BlockPos.PACKET_CODEC, HandlePositionPacket::blockPos,
                    PacketCodecs.optional(PacketCodecs.FLOAT), HandlePositionPacket::normalizedX,
                    PacketCodecs.optional(PacketCodecs.FLOAT), HandlePositionPacket::normalizedY,
                    HandlePositionPacket::new
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
        if (!(world.getBlockEntity(this.blockPos()) instanceof SpotlightLampBlockEntity blockEntity)) return;
        if (this.name().equals("big_handle")) {
            float lerpedX = MathHelper.lerp(this.normalizedX().orElse(0f), SpotlightLampBlockEntity.MIN_YAW_RANGE, SpotlightLampBlockEntity.MAX_YAW_RANGE);
            float lerpedY = MathHelper.lerp(this.normalizedY().orElse(0f), SpotlightLampBlockEntity.MIN_PITCH_RANGE, SpotlightLampBlockEntity.MAX_PITCH_RANGE);
            blockEntity.setTargetRotation(() -> new ShapeUtil.Position(lerpedX, lerpedY));
        }
        if (this.name.equals("small_handle")) {
            float lerpedY = MathHelper.lerp(this.normalizedY().orElse(0f), 0f, SpotlightLampBlockEntity.MAX_TURNING_SPEED);
            blockEntity.setSpeed(lerpedY);
        }
    }
}
