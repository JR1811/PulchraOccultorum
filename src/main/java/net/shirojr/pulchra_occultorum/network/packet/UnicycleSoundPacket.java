package net.shirojr.pulchra_occultorum.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;

public record UnicycleSoundPacket(int entityNetworkId, Boolean shouldPlay) implements CustomPayload {
    public static final CustomPayload.Id<UnicycleSoundPacket> IDENTIFIER = new CustomPayload.Id<>(PulchraOccultorum.identifierOf("unicycle_rolling"));
    public static final PacketCodec<RegistryByteBuf, UnicycleSoundPacket> CODEC = PacketCodec.of(UnicycleSoundPacket::write, UnicycleSoundPacket::new);

    public UnicycleSoundPacket(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityNetworkId);
        buf.writeBoolean(this.shouldPlay);
    }
}
