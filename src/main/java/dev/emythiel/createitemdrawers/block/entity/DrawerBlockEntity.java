package dev.emythiel.createitemdrawers.block.entity;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.config.ClientConfig;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler.ConnectedGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
public class DrawerBlockEntity extends SmartBlockEntity implements MenuProvider, TransformableBlockEntity, IHaveGoggleInformation {
    private final DrawerStorage storage;
    private final DrawerItemHandler itemHandler;

    private ItemStack upgrade = ItemStack.EMPTY;
    private boolean renderItem = true;
    private boolean renderCount = true;
    private boolean renderAdditional = true;

    public ConnectedGroup group = new ConnectedGroup();
    private IItemHandler combinedHandler;
    protected boolean reRender;


    public DrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof DrawerBlock drawer) {
            this.storage = new DrawerStorage(drawer.getSlotCount());
        } else {
            // Fallback (This shouldn't happen?)
            this.storage = new DrawerStorage(1);
        }

        this.itemHandler = new DrawerItemHandler(this.storage);
        this.itemHandler.setOnChange(this::setChangedAndSync);
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
    public boolean getRenderAdditional() { return renderAdditional; }
    public void setRenderAdditional(boolean v) { this.renderAdditional = v; }

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

        tag.putBoolean("RenderItem", renderItem);
        tag.putBoolean("RenderCount", renderCount);
        tag.putBoolean("RenderAdditional", renderAdditional);

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
            int multiplier = 1;
            if (!upgrade.isEmpty() && upgrade.getItem() instanceof CapacityUpgradeItem item) {
                multiplier = item.getTierMultiplier();
            }
            storage.setUpgradeMultiplier(multiplier);
        } else {
            upgrade = ItemStack.EMPTY;
            storage.setUpgradeMultiplier(1);
        }

        renderItem = tag.getBoolean("RenderItem");
        renderCount = tag.getBoolean("RenderCount");
        renderAdditional = tag.getBoolean("RenderAdditional");

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
        EdgeInteractionBehaviour connectivity = new EdgeInteractionBehaviour(this, ConnectedGroupHandler::toggleConnection)
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

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean SHOW_GOGGLE_TOOLTIP = ClientConfig.GOGGLE_TOOLTIP.get();
        boolean GOOGLE_TOOLTIP_REQUIRE_WRENCH = ClientConfig.GOOGLE_TOOLTIP_REQUIRE_WRENCH.get();

        if (!SHOW_GOGGLE_TOOLTIP)
            return false;

        Player player = Minecraft.getInstance().player;
        boolean holdingWrench = player.getMainHandItem().is(AllItems.WRENCH.get());
        if (GOOGLE_TOOLTIP_REQUIRE_WRENCH && !holdingWrench)
            return false;

        CreateItemDrawerLang.translate("gui.goggles.drawer_info")
            .forGoggles(tooltip);

        CreateItemDrawerLang.translate("gui.goggles.upgrade")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip);

        if (!upgrade.isEmpty()) {
            CreateItemDrawerLang.translateEmptyLine()
                .add(upgrade.getHoverName())
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);
        } else {
            CreateItemDrawerLang.translate("gui.goggles.empty")
                .style(ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);
        }

        CreateItemDrawerLang.translate("gui.goggles.storage")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip);

        for (int i = 0; i < storage.getSlotCount(); i++) {
            if (!storage.getSlot(i).getStoredItem().isEmpty()) {
                Component item = storage.getSlot(i).getStoredItem().getHoverName();
                int count = storage.getSlot(i).getCount();

                CreateItemDrawerLang.translateEmptyLine()
                    .add(item)
                    .style(ChatFormatting.AQUA)
                    .add(Component.nullToEmpty(" x" + count))
                    .forGoggles(tooltip, 1);
            } else {
                CreateItemDrawerLang.translate("gui.goggles.empty")
                    .style(ChatFormatting.DARK_GRAY)
                    .forGoggles(tooltip, 1);
            }

            CreateItemDrawerLang.translateEmptyLine()
                .add(Component.literal("⤷[").withStyle(ChatFormatting.DARK_GRAY))
                .add(CreateItemDrawerLang.translate("gui.goggles.lock")
                    .style(storage.getSlot(i).isLockMode() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .add(Component.literal("|").withStyle(ChatFormatting.DARK_GRAY))
                .add(CreateItemDrawerLang.translate("gui.goggles.void")
                    .style(storage.getSlot(i).isVoidMode() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .add(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 2);
        }

        return true;
    }

    public void applyInventoryToBlock(DrawerItemHandler wrapped) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, i < wrapped.getSlots() ? wrapped.getStackInSlot(i) : ItemStack.EMPTY);
        }
    }
}
