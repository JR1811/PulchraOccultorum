package net.shirojr.pulchra_occultorum.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import org.jetbrains.annotations.Nullable;

public record UnicycleMovementPacket(@Nullable UnicycleEntity.DirectionInput inputLeft,
                                     @Nullable UnicycleEntity.DirectionInput inputRight,
                                     @Nullable UnicycleEntity.DirectionInput inputJump)
        implements CustomPayload {

    public static final CustomPayload.Id<UnicycleMovementPacket> IDENTIFIER =
            new CustomPayload.Id<>(PulchraOccultorum.identifierOf("unicycle_moving"));
    public static final PacketCodec<RegistryByteBuf, UnicycleMovementPacket> CODEC =
            PacketCodec.of(UnicycleMovementPacket::write, UnicycleMovementPacket::new);

    public UnicycleMovementPacket(PacketByteBuf buf) {
        this(UnicycleEntity.DirectionInput.fromString(buf.readString()),
                UnicycleEntity.DirectionInput.fromString(buf.readString()),
                UnicycleEntity.DirectionInput.fromString(buf.readString())
        );
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void write(PacketByteBuf buf) {
        if (this.inputLeft == null) buf.writeString("");
        else buf.writeString(this.inputLeft.getName());
        if (this.inputRight == null) buf.writeString("");
        else buf.writeString(this.inputRight.getName());
        if (this.inputJump == null) buf.writeString("");
        else buf.writeString(this.inputJump.getName());
    }
}
