package dev.emythiel.createitemdrawers.network;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ContraptionSlotTogglePacket(
    int entityId,
    BlockPos localPos,
    int slot,
    SlotTogglePacket.ToggleMode mode,
    boolean value
) implements CustomPacketPayload {
    public static final Type<ContraptionSlotTogglePacket> TYPE =
        new Type<>(CreateItemDrawers.asResource("contraption_slot_toggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionSlotTogglePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,        ContraptionSlotTogglePacket::entityId,
            BlockPos.STREAM_CODEC,    ContraptionSlotTogglePacket::localPos,
            ByteBufCodecs.INT,        ContraptionSlotTogglePacket::slot,
            ByteBufCodecs.STRING_UTF8
                .map(SlotTogglePacket.ToggleMode::fromString,
                    m -> m.name().toLowerCase()),
            ContraptionSlotTogglePacket::mode,
            ByteBufCodecs.BOOL,       ContraptionSlotTogglePacket::value,
            ContraptionSlotTogglePacket::new
        );

    @Override @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
