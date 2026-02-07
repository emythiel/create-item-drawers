package dev.emythiel.createitemdrawers.contraption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.codec.CreateCodecs;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.network.SyncMountedStoragePacket;
import dev.emythiel.createitemdrawers.registry.ModMountedStorageTypes;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DrawerMountedStorage extends WrapperMountedItemStorage<DrawerItemHandler> {

    public static final MapCodec<DrawerMountedStorage> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("SlotCount").forGetter(s -> s.slotCount),
            ItemStack.CODEC.optionalFieldOf("Upgrade", ItemStack.EMPTY).forGetter(s -> s.upgradeItem),
            Codec.BOOL.fieldOf("RenderItems").forGetter(s -> s.renderItems),
            Codec.BOOL.fieldOf("RenderCounts").forGetter(s -> s.renderCounts),
            Codec.BOOL.fieldOf("RenderIcons").forGetter(s -> s.renderIcons),
            DrawerSlotData.CODEC.listOf().fieldOf("Slots").forGetter(s -> s.slotData)
        ).apply(instance, DrawerMountedStorage::fromCodec));

    public boolean initialized = false;
    private boolean dirty = false;
    private @Nullable Contraption currentContraption = null;

    private int slotCount = 0;
    private boolean renderItems = true;
    private boolean renderCounts = true;
    private boolean renderIcons = true;
    private ItemStack upgradeItem = ItemStack.EMPTY;
    private List<DrawerSlotData> slotData = new ArrayList<>();

    protected DrawerMountedStorage(MountedItemStorageType<?> type, DrawerItemHandler wrapped) {
        super(type, wrapped);
    }

    public DrawerMountedStorage(ItemStackHandler handler) {
        super(ModMountedStorageTypes.MOUNTED_DRAWER.get(), (DrawerItemHandler) handler);
    }

    private static class DrawerSlotData {
        static final Codec<DrawerSlotData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.optionalFieldOf("Item", ItemStack.EMPTY).forGetter(d -> d.storedItem),
                Codec.INT.fieldOf("Count").forGetter(d -> d.count),
                Codec.BOOL.fieldOf("Locked").forGetter(d -> d.lockMode),
                Codec.BOOL.fieldOf("Void").forGetter(d -> d.voidMode)
            ).apply(instance, DrawerSlotData::new));

        ItemStack storedItem;
        int count;
        boolean lockMode;
        boolean voidMode;

        public DrawerSlotData(ItemStack storedItem, int count, boolean lockMode, boolean voidMode) {
            this.storedItem = storedItem;
            this.count = count;
            this.lockMode = lockMode;
            this.voidMode = voidMode;
        }

        public DrawerSlotData() {
            this(ItemStack.EMPTY, 0, false, false);
        }
    }

    private static DrawerMountedStorage fromCodec(int slotCount, ItemStack upgrade,
                                                  boolean renderItems, boolean renderCounts, boolean renderIcons,
                                                  List<DrawerSlotData> slots) {
        DrawerStorage storage = new DrawerStorage(slotCount);

        for (int i = 0; i < Math.min(slotCount, slots.size()); i++) {
            DrawerSlotData data = slots.get(i);
            DrawerSlot slot = storage.getSlot(i);

            slot.setStoredItem(data.storedItem.copy());
            slot.setCount(data.count);
            slot.setLockMode(data.lockMode);
            slot.setVoidMode(data.voidMode);
        }

        DrawerItemHandler handler = new DrawerItemHandler(storage);
        DrawerMountedStorage mounted = new DrawerMountedStorage(ModMountedStorageTypes.MOUNTED_DRAWER.get(), handler);

        mounted.slotCount = slotCount;
        mounted.upgradeItem = upgrade;
        mounted.renderItems = renderItems;
        mounted.renderCounts = renderCounts;
        mounted.renderIcons = renderIcons;
        mounted.slotData = new ArrayList<>(slots);

        return mounted;
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureBlockInfo info) {
        // TODO: Player interaction with mounted drawer
        return false;
    }

    public static DrawerMountedStorage fromStorage(DrawerStorageBlockEntity drawerBE) {
        DrawerStorage drawerStorage = drawerBE.getStorage();
        DrawerStorage storage = new DrawerStorage(drawerStorage.getSlotCount());

        DrawerMountedStorage mounted = new DrawerMountedStorage(new DrawerItemHandler(storage));

        mounted.slotCount = drawerBE.getStorage().getSlotCount();
        mounted.renderItems = drawerBE.getRenderItems();
        mounted.renderCounts = drawerBE.getRenderCounts();
        mounted.renderIcons = drawerBE.getRenderIcons();
        mounted.upgradeItem = drawerBE.getUpgrade().copy();

        mounted.slotData.clear();
        for (int i = 0; i < mounted.slotCount; i++) {
            DrawerSlot blockSlot = drawerStorage.getSlot(i);
            DrawerSlotData data = new DrawerSlotData(
                blockSlot.getStoredItem().copy(),
                blockSlot.getCount(),
                blockSlot.isLockMode(),
                blockSlot.isVoidMode()
            );
            mounted.slotData.add(data);

            DrawerSlot slot = storage.getSlot(i);
            slot.setStoredItem(data.storedItem.copy());
            slot.setCount(data.count);
            slot.setLockMode(data.lockMode);
            slot.setVoidMode(data.voidMode);
        }

        return mounted;
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof DrawerStorageBlockEntity drawer) {
            drawer.setUpgrade(upgradeItem);
            drawer.setRenderItems(renderItems);
            drawer.setRenderCounts(renderCounts);
            drawer.setRenderIcons(renderIcons);

            for (int i = 0; i < Math.min(slotData.size(), drawer.getStorage().getSlotCount()); i++) {
                DrawerSlotData data = slotData.get(i);
                DrawerSlot slot = drawer.getStorage().getSlot(i);

                if (data != null) {
                    slot.setStoredItem(data.storedItem.copy());
                    slot.setCount(data.count);
                    slot.setLockMode(data.lockMode);
                    slot.setVoidMode(data.voidMode);
                }
            }

            drawer.setChangedAndSync();
        }

        currentContraption = null;
    }

    @Override @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot < 0 || slot >= wrapped.getSlots())
            return stack;

        ItemStack result = wrapped.insertItem(slot, stack, simulate);

        if (!simulate && (result.isEmpty() || result.getCount() != stack.getCount())) {
            markDirty();
        }

        return result;
    }

    @Override @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= wrapped.getSlots())
            return ItemStack.EMPTY;

        ItemStack result = wrapped.extractItem(slot, amount, simulate);

        if (!simulate && !result.isEmpty()) {
            markDirty();
        }

        return result;
    }

    public void updateClientStorageData(MovementContext context, HolderLookup.@NotNull Provider provider) {
        if (!initialized || context.world.isClientSide()) return;

        CompoundTag tag = new CompoundTag();

        if (!upgradeItem.isEmpty()) {
            tag.put("Upgrade", upgradeItem.save(provider));
        }

        tag.putBoolean("RenderItems", renderItems);
        tag.putBoolean("RenderCounts", renderCounts);
        tag.putBoolean("RenderIcons", renderIcons);

        ListTag slotsTag = new ListTag();
        for (DrawerSlotData data : slotData) {
            CompoundTag slotTag = new CompoundTag();
            if (!data.storedItem.isEmpty()) {
                slotTag.put("Item", data.storedItem.save(provider));
            }
            slotTag.putInt("Count", data.count);
            slotTag.putBoolean("Locked", data.lockMode);
            slotTag.putBoolean("Void", data.voidMode);
            slotsTag.add(slotTag);
        }
        tag.put("Slots", slotsTag);
        tag.putInt("SlotCount", slotCount);

        PacketDistributor.sendToPlayersTrackingEntity(
            context.contraption.entity, new SyncMountedStoragePacket(
                context.contraption.entity.getId(),
                context.localPos,
                tag
            )
        );
        markClean();
    }

    public void initBlockEntityData(MovementContext context) {
        if (initialized || context.world.isClientSide()) return;

        CompoundTag tag = new CompoundTag();

        tag.putInt("SlotCount", slotCount);
        tag.putBoolean("RenderItems", renderItems);
        tag.putBoolean("RenderCounts", renderCounts);
        tag.putBoolean("RenderIcons", renderIcons);

        if (!upgradeItem.isEmpty()) {
            tag.put("Upgrade", upgradeItem.save(context.world.registryAccess()));
        }

        ListTag slotsTag = new ListTag();
        for (DrawerSlotData data : slotData) {
            CompoundTag slotTag = new CompoundTag();
            if (!data.storedItem.isEmpty()) {
                slotTag.put("Item", data.storedItem.save(context.world.registryAccess()));
            }
            slotTag.putInt("Count", data.count);
            slotTag.putBoolean("Locked", data.lockMode);
            slotTag.putBoolean("Void", data.voidMode);
            slotsTag.add(slotTag);
        }
        tag.put("Slots", slotsTag);

        ConnectedGroupHandler.ConnectedGroup newGroup = new ConnectedGroupHandler.ConnectedGroup();
        CompoundTag groupTag = new CompoundTag();
        newGroup.write(groupTag);
        tag.put("ConnectedGroup", groupTag);

        StructureTemplate.StructureBlockInfo updatedInfo = new StructureTemplate.StructureBlockInfo(
            context.localPos,
            context.state,
            tag
        );
        context.contraption.getBlocks().put(context.localPos, updatedInfo);

        context.blockEntityData = tag;
        this.currentContraption = context.contraption.entity.getContraption();

        PacketDistributor.sendToPlayersTrackingEntity(
            context.contraption.entity, new SyncMountedStoragePacket(
                context.contraption.entity.getId(),
                context.localPos,
                tag.copy()
            )
        );
        initialized = true;
    }

    public void markDirty() {
        this.dirty = true;
        updateSlotDataFromHandler();
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    private void updateSlotDataFromHandler() {
        while (slotData.size() < wrapped.getSlots()) {
            slotData.add(new DrawerSlotData());
        }

        for (int i = 0; i < Math.min(slotData.size(), wrapped.getSlots()); i++) {
            DrawerSlotData data = slotData.get(i);
            ItemStack stack = wrapped.getStackInSlot(i);

            if (stack.isEmpty()) {
                data.storedItem = ItemStack.EMPTY;
                data.count = 0;
            } else {
                data.storedItem = stack.copyWithCount(1);
                data.count = stack.getCount();
            }
        }
    }

    public void onSlotChanged(int slot) {
        if (slot >= 0 && slot < slotData.size()) {
            updateSlotDataFromHandler();
            markDirty();
        }
    }
}
