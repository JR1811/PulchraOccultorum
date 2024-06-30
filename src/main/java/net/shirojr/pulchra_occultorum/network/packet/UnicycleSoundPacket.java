package net.shirojr.pulchra_occultorum.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;

public record UnicycleSoundPacket(int entityNetworkId, boolean shouldPlay) implements CustomPayload {

    public static final CustomPayload.Id<UnicycleSoundPacket> IDENTIFIER = new CustomPayload.Id<>(PulchraOccultorum.identifierOf("unicycle_rolling"));

    public static final PacketCodec<RegistryByteBuf, UnicycleSoundPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, UnicycleSoundPacket::entityNetworkId,
            PacketCodecs.BOOL, UnicycleSoundPacket::shouldPlay,
            UnicycleSoundPacket::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }


}
