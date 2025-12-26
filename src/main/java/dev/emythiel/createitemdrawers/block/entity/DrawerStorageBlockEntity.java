package dev.emythiel.createitemdrawers.block.entity;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlock;
import dev.emythiel.createitemdrawers.block.base.BaseDrawerBlockEntity;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.registry.ModConfigs;
import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DrawerStorageBlockEntity extends BaseDrawerBlockEntity implements MenuProvider, IHaveGoggleInformation {
    private final DrawerStorage storage;
    private final DrawerItemHandler itemHandler;

    private ItemStack upgrade = ItemStack.EMPTY;
    private boolean renderItem = true;
    private boolean renderCount = true;
    private boolean renderAdditional = true;

    public DrawerStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof DrawerStorageBlock drawer) {
            this.storage = new DrawerStorage(drawer.getSlotCount());
        } else {
            // Fallback (This shouldn't happen?)
            this.storage = new DrawerStorage(1);
        }

        this.itemHandler = new DrawerItemHandler(this.storage);
        this.itemHandler.setOnChange(this::setChangedAndSync);
    }

    public DrawerStorage getStorage() {
        return storage;
    }

    @Override
    public DrawerItemHandler getLocalHandler() {
        return itemHandler;
    }

    @Override
    public BaseDrawerBlockEntity getController() {
        return ConnectedGroupHandler.getController(this);
    }

    public ItemStack getUpgrade() {
        return upgrade;
    }
    public void setUpgrade(ItemStack stack) {
        this.upgrade = stack.copy();

        int multiplier = 1;

        if (!upgrade.isEmpty() && upgrade.getItem() instanceof CapacityUpgradeItem item) {
            multiplier = item.getTierMultiplier();
        }

        storage.setUpgradeMultiplier(multiplier);
        setChangedAndSync();
    }

    public boolean getRenderItems() {
        return renderItem;
    }
    public void setRenderItems(boolean v) {
        this.renderItem = v;
    }

    public boolean getRenderCounts() {
        return renderCount;
    }
    public void setRenderCounts(boolean v) {
        this.renderCount = v;
    }

    public boolean getRenderAdditional() {
        return renderAdditional;
    }
    public void setRenderAdditional(boolean v) {
        this.renderAdditional = v;
    }

    @Override
    protected void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider, boolean clientPacket) {
        super.write(tag, provider, clientPacket);

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
    }

    @Override
    protected void read(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider, boolean clientPacket) {
        super.read(tag, provider, clientPacket);

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
        return CreateItemDrawerLang.translate("gui.drawer_" + slots).component();
    }

    // Goggle tooltip TODO: is this proper way? Server can't get client configs, but is forcing OnlyIn(Dist.CLIENT) right?
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean SHOW_GOGGLE_TOOLTIP = ModConfigs.client().goggleTooltip.get();
        boolean GOOGLE_TOOLTIP_REQUIRE_WRENCH = ModConfigs.client().goggleTooltipRequiresWrench.get();

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
}
