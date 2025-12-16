package dev.emythiel.createitemdrawers.contraption;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.codec.CreateCodecs;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModMountedStorageTypes;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class MountedStorage extends WrapperMountedItemStorage<ItemStackHandler> {

    public static final MapCodec<MountedStorage> CODEC = CreateCodecs.ITEM_STACK_HANDLER.xmap(
        MountedStorage::new, storage -> storage.wrapped
    ).fieldOf("value");

    public boolean initialized = false;
    private boolean dirty = false;
    @Nullable
    private Contraption currentContraption = null;

    public MountedStorage(ItemStackHandler handler) {
        super(ModMountedStorageTypes.MOUNTED_DRAWER.get(), handler);
    }

    public static MountedStorage fromStorage(DrawerBlockEntity be) {
        DrawerStorage storage = be.getStorage();
        int slotCount = storage.getSlotCount();

        ItemStackHandler handler = new ItemStackHandler(slotCount + 2);

        for (int i = 0; i < slotCount; i++) {
            DrawerSlot slot = storage.getSlot(i);
            if (!slot.isEmpty()) {
                ItemStack stack = slot.getStoredItem().copy();
                stack.setCount(slot.getCount());

                CompoundTag slotData = new CompoundTag();
                slotData.putBoolean("Locked", slot.isLockMode());
                slotData.putBoolean("Void", slot.isVoidMode());

                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(slotData));

                handler.setStackInSlot(i, stack);
            }
        }

        handler.setStackInSlot(slotCount, be.getUpgrade().copy());

        ItemStack metadata = new ItemStack(Items.PAPER);
        CompoundTag metaData = new CompoundTag();
        metaData.putInt("SlotCount", slotCount);
        metaData.putBoolean("RenderItem", be.getRenderItems());
        metaData.putBoolean("RenderCount", be.getRenderCounts());
        metaData.putBoolean("RenderAdditional", be.getRenderAdditional());

        metadata.set(DataComponents.CUSTOM_DATA, CustomData.of(metaData));

        handler.setStackInSlot(slotCount + 1, metadata);

        MountedStorage mountedStorage = new MountedStorage(handler);
        mountedStorage.initialized = false;
        mountedStorage.markDirty();

        return mountedStorage;
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureBlockInfo info) {
        // TODO: Player interaction with mounted drawer
        return false;
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, BlockEntity be) {
        if (be instanceof DrawerBlockEntity drawer) {
            int totalSlots = wrapped.getSlots();
            if (totalSlots < 2) return;

            int slotCount = getSlotCountFromHandler();

            ItemStack upgrade = wrapped.getStackInSlot(slotCount);
            drawer.setUpgrade(upgrade.copy());

            for (int i = 0; i < Math.min(slotCount, drawer.getStorage().getSlotCount()); i++) {
                ItemStack stack = wrapped.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    drawer.getLocalHandler().insertItem(i, stack, false);

                    CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                    if (customData != null) {
                        CompoundTag slotData = customData.copyTag();
                        DrawerSlot slot = drawer.getStorage().getSlot(i);
                        slot.setLockMode(slotData.getBoolean("Locked"));
                        slot.setVoidMode(slotData.getBoolean("Void"));
                    }
                }
            }

            ItemStack metadata = wrapped.getStackInSlot(slotCount + 1);
            CustomData metaCustomData = metadata.get(DataComponents.CUSTOM_DATA);
            if (metaCustomData != null) {
                CompoundTag metaData = metaCustomData.copyTag();
                drawer.setRenderItems(metaData.getBoolean("RenderItem"));
                drawer.setRenderCounts(metaData.getBoolean("RenderCount"));
                drawer.setRenderAdditional(metaData.getBoolean("RenderAdditional"));
            }

            drawer.setChangedAndSync();
        }

        currentContraption = null;
        initialized = false;
    }

    private int getSlotCountFromHandler() {
        int totalSlots = wrapped.getSlots();

        if (totalSlots >= 2) {
            ItemStack metadata = wrapped.getStackInSlot(totalSlots - 1);
            CustomData customData = metadata.get(DataComponents.CUSTOM_DATA);

            if (customData != null) {
                CompoundTag metaData = customData.copyTag();
                return metaData.getInt("SlotCount");
            }

            return totalSlots - 2;
        }

        return 0;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int slotCount = getSlotCountFromHandler();

        if (slot >= slotCount) {
            return stack;
        }

        ItemStack result = super.insertItem(slot, stack, simulate);
        if (!simulate && result.getCount() != stack.getCount()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int slotCount = getSlotCountFromHandler();

        if (slot >= slotCount) {
            return ItemStack.EMPTY;
        }

        ItemStack result = super.extractItem(slot, amount, simulate);
        if (!simulate && !result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }
}
