package dev.emythiel.createitemdrawers.network;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SlotTogglePacket(BlockPos pos, int slot, boolean lock, boolean value) implements CustomPacketPayload {

    public static final Type<SlotTogglePacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "slot_toggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlotTogglePacket> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, SlotTogglePacket::pos,
            ByteBufCodecs.INT, SlotTogglePacket::slot,
            ByteBufCodecs.BOOL, SlotTogglePacket::lock,
            ByteBufCodecs.BOOL, SlotTogglePacket::value,
            SlotTogglePacket::new
        );

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
