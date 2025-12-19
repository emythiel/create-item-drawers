package dev.emythiel.createitemdrawers.network;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SyncMountedStoragePacket(int contraptionId, BlockPos localPos, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<SyncMountedStoragePacket> TYPE = new Type<>(CreateItemDrawers.asResource("sync_mounted_storage"));

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMountedStoragePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, SyncMountedStoragePacket::contraptionId,
            BlockPos.STREAM_CODEC, SyncMountedStoragePacket::localPos,
            ByteBufCodecs.COMPOUND_TAG, SyncMountedStoragePacket::tag,
            SyncMountedStoragePacket::new
        );
}
