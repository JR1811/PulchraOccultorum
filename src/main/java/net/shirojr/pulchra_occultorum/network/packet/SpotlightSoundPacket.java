package net.shirojr.pulchra_occultorum.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.sound.SoundManager;
import net.shirojr.pulchra_occultorum.sound.instance.SpotlightLampRotationSoundInstance;

public record SpotlightSoundPacket(BlockPos pos, boolean shouldPlay) implements CustomPayload {

    public static final Id<SpotlightSoundPacket> IDENTIFIER = new Id<>(PulchraOccultorum.identifierOf("spotlight_rotating"));

    public static final PacketCodec<RegistryByteBuf, SpotlightSoundPacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, SpotlightSoundPacket::pos,
            PacketCodecs.BOOL, SpotlightSoundPacket::shouldPlay,
            SpotlightSoundPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(ServerPlayerEntity targetedPlayer) {
        ServerPlayNetworking.send(targetedPlayer, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientWorld world = context.client().world;
        if (world == null) return;
        if (!(world.getBlockEntity(this.pos()) instanceof SpotlightLampBlockEntity blockEntity)) return;
        SoundManager soundManager = SoundManager.getInstance();
        SpotlightLampRotationSoundInstance soundInstance = new SpotlightLampRotationSoundInstance(blockEntity);
        if (this.shouldPlay()) soundManager.play(blockEntity, soundInstance);
        else soundManager.stopAll(blockEntity);
    }
}
