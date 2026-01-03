package dev.emythiel.createitemdrawers.network.handler;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.network.SyncMountedStoragePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() { return INSTANCE; }

    public void handleSyncMountedStorage(final SyncMountedStoragePacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = ctx.player().level();
            Entity entity = level.getEntity(packet.contraptionId());

            if (entity instanceof AbstractContraptionEntity contraptionEntity) {
                StructureTemplate.StructureBlockInfo blockInfo = contraptionEntity.getContraption().getBlocks().get(packet.localPos());

                if (blockInfo == null) {
                    CreateItemDrawers.LOGGER.warn(
                        "Could not find block info at position {} for contraption {}",
                        packet.localPos(), packet.contraptionId()
                    );
                    return;
                }

                CompoundTag newTag = blockInfo.nbt();
                CompoundTag packetTag = packet.tag();

                if (packetTag.contains("Upgrade", CompoundTag.TAG_COMPOUND)) {
                    newTag.put("Upgrade", packetTag.getCompound("Upgrade"));
                } else {
                    newTag.remove("Upgrade");
                }

                newTag.putBoolean("RenderItems", packetTag.getBoolean("RenderItems"));
                newTag.putBoolean("RenderCounts", packetTag.getBoolean("RenderCounts"));
                newTag.putBoolean("RenderIcons", packetTag.getBoolean("RenderIcons"));

                if (packetTag.contains("Slots", CompoundTag.TAG_LIST)) {
                    ListTag slotsTag = packetTag.getList("Slots", CompoundTag.TAG_COMPOUND);
                    newTag.put("Slots", slotsTag);
                }

                if (packetTag.contains("SlotCount", CompoundTag.TAG_INT)) {
                    newTag.putInt("SlotCount", packetTag.getInt("SlotCount"));
                }

                StructureTemplate.StructureBlockInfo newInfo = new StructureTemplate.StructureBlockInfo(
                    blockInfo.pos(),
                    blockInfo.state(),
                    newTag
                );

                contraptionEntity.getContraption().getBlocks().put(packet.localPos(), newInfo);

                contraptionEntity.getContraption().resetClientContraption();
            }
        });
    }
}
