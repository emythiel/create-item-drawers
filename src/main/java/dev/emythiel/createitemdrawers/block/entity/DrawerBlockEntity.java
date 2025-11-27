package dev.emythiel.createitemdrawers.block.entity;

import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * DrawerBlockEntity represents a placed drawer block in the world.
 * Responsibilities:
 *   ✔ Hold a DrawerStorage instance (manages per-slot data)
 *   ✔ Track drawer-wide render settings (show item and/or count)
 *   ✔ Save and load all storage + settings to NBT
 *   ✔ Provide block update sync to clients
 */
public class DrawerBlockEntity extends BaseBlockEntity {
    private final DrawerStorage storage;
    private final DrawerItemHandler itemHandler;

    private boolean renderItem = true;
    private boolean renderCount = true;

    public DrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof DrawerBlock drawer) {
            this.storage = new DrawerStorage(drawer.getSlotCount());
        } else {
            // Fallback (This shouldn't happen?)
            this.storage = new DrawerStorage(1);
        }

        this.itemHandler = new DrawerItemHandler(this);
    }

    public DrawerStorage getStorage() {
        return storage;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return itemHandler;
    }

    public boolean getRenderItems() { return renderItem; }
    public void setRenderItems(boolean v) { this.renderItem = v; }
    public boolean getRenderCounts() { return renderCount; }
    public void setRenderCounts(boolean v) { this.renderCount = v; }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        // save flags
        tag.putBoolean("RenderItem", renderItem);
        tag.putBoolean("RenderCount", renderCount);

        // save slots
        ListTag list = new ListTag();
        for (int i = 0; i < storage.getSlotCount(); i++) {
            CompoundTag slotTag = new CompoundTag();
            storage.getSlot(i).save(slotTag, provider);
            list.add(slotTag);
        }
        tag.put("Slots", list);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        renderItem = tag.getBoolean("RenderItem");
        renderCount = tag.getBoolean("RenderCount");

        ListTag list = tag.getList("Slots", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            storage.getSlot(i).load(list.getCompound(i), provider);
        }
    }

    public void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }
}
