package dev.emythiel.createitemdrawers.block.entity;

import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
public class DrawerBlockEntity extends BaseBlockEntity implements MenuProvider {
    private final DrawerStorage storage;
    private final DrawerItemHandler itemHandler;

    private ItemStack upgrade = ItemStack.EMPTY;
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

    public ItemStack getUpgrade() { return upgrade; }
    public void setUpgrade(ItemStack stack) {
        this.upgrade = stack.copy();

        int multiplier = 1;

        if (!upgrade.isEmpty() && upgrade.getItem() instanceof CapacityUpgradeItem item) {
            multiplier = item.getTierMultiplier();
        }

        storage.setUpgradeMultiplier(multiplier);
        setChangedAndSync();
    }


    public boolean getRenderItems() { return renderItem; }
    public void setRenderItems(boolean v) { this.renderItem = v; }
    public boolean getRenderCounts() { return renderCount; }
    public void setRenderCounts(boolean v) { this.renderCount = v; }

    private int renderMode = 0; // 0 = all, 1 = items only, 2 = none
    public int getRenderMode() {
        return renderMode;
    }
    public void setRenderMode(int mode) {
        this.renderMode = mode;
        setChangedAndSync();
    }
    public void applyRenderMode(int mode) {
        this.renderMode = mode;

        switch (mode) {
            case 0 -> { renderItem = true; renderCount = true; }
            case 1 -> { renderItem = true; renderCount = false; }
            case 2 -> { renderItem = false; renderCount = false; }
        }

        setChangedAndSync();
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);

        if (!upgrade.isEmpty()) {
            tag.put("Upgrade", upgrade.save(provider));
        }

        tag.putInt("RenderMode", renderMode);

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
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains("Upgrade")) {
            upgrade = ItemStack.parseOptional(provider, tag.getCompound("Upgrade"));
            setUpgrade(upgrade);
        } else {
            upgrade = ItemStack.EMPTY;
            setUpgrade(ItemStack.EMPTY);
        }

        renderMode = tag.getInt("RenderMode");
        applyRenderMode(renderMode);

        // load slots
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
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    // GUI handling
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return DrawerMenu.create(id, inv, this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        int slots = storage.getSlotCount();
        return Component.translatable("gui.create_item_drawers.drawer_" + slots);
    }

    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(
            (id, inv, player) -> DrawerMenu.create(id, inv, this),
            Component.literal("Drawer Settings")
        );
    }


}
