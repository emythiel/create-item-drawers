package dev.emythiel.createitemdrawers.network;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SlotTogglePacket(BlockPos pos, int slot, ToggleMode mode, boolean value) implements CustomPacketPayload {

    public static final Type<SlotTogglePacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "slot_toggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlotTogglePacket> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, SlotTogglePacket::pos,
            ByteBufCodecs.INT, SlotTogglePacket::slot,
            ByteBufCodecs.STRING_UTF8
                .map(ToggleMode::fromString,
                    mode -> mode.name().toLowerCase()
                ), SlotTogglePacket::mode,
            ByteBufCodecs.BOOL, SlotTogglePacket::value,
            SlotTogglePacket::new
        );

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum ToggleMode {
        LOCK, VOID, ITEMS, COUNTS, SETTINGS;

        public static ToggleMode fromString(String name) {
            return ToggleMode.valueOf(name.toUpperCase());
        }
    }
}
