package dev.emythiel.createitemdrawers.network;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DrawerConfigPacket(BlockPos pos, int renderMode) implements CustomPacketPayload {

    public static final Type<DrawerConfigPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "drawer_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawerConfigPacket> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, DrawerConfigPacket::pos,
            ByteBufCodecs.INT, DrawerConfigPacket::renderMode,
            DrawerConfigPacket::new
        );

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
