package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import dev.emythiel.createitemdrawers.registry.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class DrawerMenu extends MenuBase<DrawerStorageBlockEntity> {

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, DrawerStorageBlockEntity be) {
        super(type, id, inv, be);
    }

    public static DrawerMenu create (int id, Inventory inv, DrawerStorageBlockEntity be) {
        return new DrawerMenu(ModMenuTypes.DRAWER_MENU.get(), id, inv, be);
    }

    @Override
    protected DrawerStorageBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockEntity be = level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof DrawerStorageBlockEntity drawerBE) {
            //drawerBE.readClient(extraData.readNbt(), extraData.registryAccess());
            return drawerBE;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(DrawerStorageBlockEntity be) {}

    @Override
    protected void addSlots() {
        // Player inventory
        this.addPlayerSlots(8, 131);

        // Upgrade slot
        DrawerStorageBlockEntity be = contentHolder;
        this.addSlot(new UpgradeSlot(be, 24, 39));

        // Drawer slots
        int count = be.getStorage().getSlotCount();

        if (count == 1) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 110, 39));
        }
        else if (count == 2) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 110, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 1, 110, 51));
        }
        else if (count == 4) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 98, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 1, 122, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 2, 98, 51));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 3, 122, 51));
        }
    }

    @Override
    protected void saveData(DrawerStorageBlockEntity be) {}

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy(); // Keeping copy for return value

        // Player slots at indices 0-35, Upgrade slot at 36, Storage slots at 37+
        int upgradeSlot = 36;

        if (index < upgradeSlot) {
            // Clicked player slot, try to move to upgrade slot
            if (!(slotStack.getItem() instanceof CapacityUpgradeItem))
                return ItemStack.EMPTY;

            if (!this.moveItemStackTo(slotStack, 36, 37, false))
                return ItemStack.EMPTY;
        } else if (index == upgradeSlot) {
            // Clicked upgrade slot, try to move to player slot
            if (!this.moveItemStackTo(slotStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            // Clicked on drawer slot, do nothing
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return originalStack;
    }
}
