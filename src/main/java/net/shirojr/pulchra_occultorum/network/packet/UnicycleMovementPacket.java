package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;

import java.util.Optional;

public record UnicycleMovementPacket(Optional<UnicycleEntity.DirectionInput> inputLeft,
                                     Optional<UnicycleEntity.DirectionInput> inputRight,
                                     Optional<UnicycleEntity.DirectionInput> inputJump)
        implements CustomPayload {

    public static final CustomPayload.Id<UnicycleMovementPacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.getId("unicycle_moving"));

    public static final PacketCodec<RegistryByteBuf, UnicycleMovementPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.optional(PacketCodecs.BYTE.xmap(index ->
                            UnicycleEntity.DirectionInput.values()[index],
                    directionInput -> (byte) directionInput.ordinal())),
            UnicycleMovementPacket::inputLeft,
            PacketCodecs.optional(PacketCodecs.BYTE.xmap(index ->
                            UnicycleEntity.DirectionInput.values()[index],
                    directionInput -> (byte) directionInput.ordinal())),
            UnicycleMovementPacket::inputRight,
            PacketCodecs.optional(PacketCodecs.BYTE.xmap(index ->
                            UnicycleEntity.DirectionInput.values()[index],
                    directionInput -> (byte) directionInput.ordinal())),
            UnicycleMovementPacket::inputJump,
            UnicycleMovementPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (!(player.getVehicle() instanceof UnicycleEntity unicycleEntity)) return;
        UnicycleEntity.DirectionInput[] input = new UnicycleEntity.DirectionInput[]{null, null, null};

        if (this.inputLeft().isPresent()) input[0] = this.inputLeft().get();
        if (this.inputRight().isPresent()) input[1] = this.inputRight().get();
        if (this.inputJump().isPresent()) input[2] = this.inputJump().get();
        unicycleEntity.setDirectionInputs(input);
    }
}
