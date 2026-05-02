package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDrawerMenu<T extends IDrawerGuiHolder> extends MenuBase<T> {

    protected AbstractDrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        super(type, id, inv, buf);
    }

    protected AbstractDrawerMenu(MenuType<?> type, int id, Inventory inv, T holder) {
        super(type, id, inv, holder);
    }


    public IDrawerGuiHolder getHolder() {
        return contentHolder;
    }

    @Override
    protected void initAndReadInventory(T holder) {}

    @Override
    protected void saveData(T holder) {}

    @Override
    protected void addSlots() {
        addPlayerSlots(8, 131);

        IDrawerGuiHolder holder = contentHolder;
        addSlot(new UpgradeSlot(holder, 24, 39));

        int count = holder.getSlotCount();

        if (count == 1) {
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 0, 110, 39));
        } else if (count == 2) {
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 0, 110, 27));
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 1, 110, 51));
        } else if (count == 4) {
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 0, 98, 27));
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 1, 122, 27));
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 2, 98, 51));
            addSlot(new DrawerSlotItemHandler(holder.getLocalHandler(), 3, 122, 51));
        }
    }

    @Override @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        // Indices: 0–35 player inventory, 36 upgrade slot, 37+ drawer slots
        int upgradeSlot = 36;

        if (index < upgradeSlot) {
            if (!(slotStack.getItem() instanceof CapacityUpgradeItem))
                return ItemStack.EMPTY;
            if (!this.moveItemStackTo(slotStack, 36, 37, false))
                return ItemStack.EMPTY;
        } else if (index == upgradeSlot) {
            if (!this.moveItemStackTo(slotStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return originalStack;
    }


}
