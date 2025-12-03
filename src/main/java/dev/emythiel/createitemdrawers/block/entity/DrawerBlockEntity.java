package dev.emythiel.createitemdrawers.block.entity;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler.ConnectedGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * DrawerBlockEntity represents a placed drawer block in the world.
 * Responsibilities:
 *   ✔ Hold a DrawerStorage instance (manages per-slot data)
 *   ✔ Track drawer-wide render settings (show item and/or count)
 *   ✔ Save and load all storage + settings to NBT
 *   ✔ Provide block update sync to clients
 */
public class DrawerBlockEntity extends SmartBlockEntity implements MenuProvider, TransformableBlockEntity {
    private final DrawerStorage storage;
    private final DrawerItemHandler itemHandler;

    public ConnectedGroup group = new ConnectedGroup();
    private IItemHandler combinedHandler;
    protected boolean reRender;
    private EdgeInteractionBehaviour connectivity;

    private ItemStack upgrade = ItemStack.EMPTY;
    private boolean renderItem = true;
    private boolean renderCount = true;
    private int renderMode = 0; // 0 = all, 1 = items only, 2 = none

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

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.DRAWER_BLOCK_ENTITY.get(),
            DrawerBlockEntity::getItemHandler
        );
    }

    public DrawerStorage getStorage() {
        return storage;
    }

    public DrawerItemHandler getLocalHandler() {
        return itemHandler;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (combinedHandler == null) {
            combinedHandler = ConnectedGroupHandler.buildCombinedHandler(this);
            if (combinedHandler == null) {
                combinedHandler = itemHandler;
            }
        }
        return combinedHandler;
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

    public ConnectedGroup getGroup() { return group; }

    public boolean getRenderItems() { return renderItem; }
    public void setRenderItems(boolean v) { this.renderItem = v; }
    public boolean getRenderCounts() { return renderCount; }
    public void setRenderCounts(boolean v) { this.renderCount = v; }

    public int getRenderMode() { return renderMode; }

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

    public void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeSafe(tag, provider);
        if (group == null)
            return;

        CompoundTag groupTag = new CompoundTag();
        group.write(groupTag);
        tag.put("ConnectedGroup", groupTag);
    }

    @Override
    protected void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider, boolean clientPacket) {

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

        CompoundTag groupTag = new CompoundTag();
        group.write(groupTag);
        tag.put("ConnectedGroup", groupTag);

        super.write(tag, provider, clientPacket);

        if (clientPacket && reRender) {
            tag.putBoolean("Redraw", true);
            reRender = false;
        }
    }

    @Override
    protected void read(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider, boolean clientPacket) {

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

        group.read(tag.getCompound("ConnectedGroup"));

        super.read(tag, provider, clientPacket);


        if (!clientPacket)
            return;
        if (tag.contains("Redraw")) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
        }
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

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        connectivity = new EdgeInteractionBehaviour(this, ConnectedGroupHandler::toggleConnection)
            .connectivity(ConnectedGroupHandler::shouldConnect)
            .require(AllItems.WRENCH.get());
        behaviours.add(connectivity);
    }

    public void connectivityChanged() {
        reRender = true;
        sendData();
        combinedHandler = null;
        invalidateCapabilities();
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        group.offsets.replaceAll(transform::applyWithoutOffset);
        notifyUpdate();
    }
}
