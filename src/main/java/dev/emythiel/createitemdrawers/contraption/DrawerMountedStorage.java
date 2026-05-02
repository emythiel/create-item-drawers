package dev.emythiel.createitemdrawers.contraption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.codec.CreateCodecs;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.gui.IDrawerGuiHolder;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.network.SyncMountedStoragePacket;
import dev.emythiel.createitemdrawers.registry.ModMenuTypes;
import dev.emythiel.createitemdrawers.registry.ModMountedStorageTypes;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DrawerMountedStorage extends WrapperMountedItemStorage<DrawerItemHandler> implements IDrawerGuiHolder {

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
        handler.setOnChange(mounted::markDirty);

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
        int entityId = contraption.entity.getId();
        BlockPos localPos = info.pos();

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return dev.emythiel.createitemdrawers.util.CreateItemDrawerLang
                    .translate("gui.drawer_" + slotCount).component();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                return new ContraptionDrawerMenu(
                    ModMenuTypes.CONTRAPTION_DRAWER_MENU.get(), id, inv,
                    DrawerMountedStorage.this, entityId, localPos
                );
            }
        }, buf -> {
            buf.writeInt(entityId);
            buf.writeBlockPos(localPos);
        });

        return true;
    }

    @Override
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    @Nullable
    public DrawerSlot getDrawerSlot(int slot) {
        return wrapped.getDrawerSlot(slot);
    }

    @Override
    public DrawerItemHandler getLocalHandler() {
        return wrapped;
    }

    @Override
    public ItemStack getUpgrade() {
        return upgradeItem;
    }

    @Override
    public void setUpgrade(ItemStack stack) {
        this.upgradeItem = stack.copy();
        int multiplier = 1;
        if (!upgradeItem.isEmpty() && upgradeItem.getItem() instanceof CapacityUpgradeItem item) {
            multiplier = item.getTierMultiplier();
        }
        wrapped.getStorage().setUpgradeMultiplier(multiplier);
        markDirty();
    }

    @Override
    public boolean getRenderItems() { return renderItems; }
    @Override
    public void setRenderItems(boolean v) { renderItems = v; }

    @Override
    public boolean getRenderCounts() { return renderCounts; }
    @Override
    public void setRenderCounts(boolean v) { renderCounts = v; }

    @Override
    public boolean getRenderIcons() { return renderIcons; }
    @Override
    public void setRenderIcons(boolean v) { renderIcons = v; }


    public static DrawerMountedStorage fromBlockInfoNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        int count = nbt.contains("SlotCount") ? nbt.getInt("SlotCount") : 1;

        DrawerStorage storage = new DrawerStorage(count);

        if (nbt.contains("Slots")) {
            net.minecraft.nbt.ListTag slots = nbt.getList("Slots", CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(count, slots.size()); i++) {
                CompoundTag slotTag = slots.getCompound(i);
                DrawerSlot slot = storage.getSlot(i);
                if (slotTag.contains("Item"))
                    slot.setStoredItem(ItemStack.parseOptional(provider, slotTag.getCompound("Item")));
                slot.setCount(slotTag.getInt("Count"));
                slot.setLockMode(slotTag.getBoolean("Locked"));
                slot.setVoidMode(slotTag.getBoolean("Void"));
            }
        }

        ItemStack upgrade = ItemStack.EMPTY;
        if (nbt.contains("Upgrade"))
            upgrade = ItemStack.parseOptional(provider, nbt.getCompound("Upgrade"));

        if (!upgrade.isEmpty() && upgrade.getItem() instanceof CapacityUpgradeItem item)
            storage.setUpgradeMultiplier(item.getTierMultiplier());

        DrawerItemHandler handler = new DrawerItemHandler(storage);
        DrawerMountedStorage mounted = new DrawerMountedStorage(
            ModMountedStorageTypes.MOUNTED_DRAWER.get(), handler
        );

        mounted.slotCount = count;
        mounted.upgradeItem = upgrade;
        mounted.renderItems = nbt.getBoolean("RenderItems");
        mounted.renderCounts = nbt.getBoolean("RenderCounts");
        mounted.renderIcons = nbt.getBoolean("RenderIcons");

        for (int i = 0; i < count; i++) {
            DrawerSlot s = storage.getSlot(i);
            mounted.slotData.add(new DrawerSlotData(
                s.getStoredItem().isEmpty() ? ItemStack.EMPTY : s.getStoredItem().copyWithCount(1),
                s.getCount(), s.isLockMode(), s.isVoidMode()
            ));
        }

        return mounted;
    }


    public static DrawerMountedStorage fromStorage(DrawerStorageBlockEntity drawerBE) {
        DrawerStorage drawerStorage = drawerBE.getStorage();
        DrawerStorage storage = new DrawerStorage(drawerStorage.getSlotCount());

        DrawerItemHandler handler = new DrawerItemHandler(storage);
        DrawerMountedStorage mounted = new DrawerMountedStorage(ModMountedStorageTypes.MOUNTED_DRAWER.get(), handler);
        handler.setOnChange(mounted::markDirty);

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
            DrawerSlot drawerSlot = wrapped.getDrawerSlot(i);
            if (drawerSlot == null) continue;

            data.storedItem = drawerSlot.getStoredItem().isEmpty()
                ? ItemStack.EMPTY
                : drawerSlot.getStoredItem().copyWithCount(1);
            data.count = drawerSlot.getCount();
            // Persist lock/void so they survive unmount even if toggled while mounted
            data.lockMode = drawerSlot.isLockMode();
            data.voidMode = drawerSlot.isVoidMode();
        }
    }

    public void onSlotChanged(int slot) {
        if (slot >= 0 && slot < slotData.size()) {
            updateSlotDataFromHandler();
            markDirty();
        }
    }
}
