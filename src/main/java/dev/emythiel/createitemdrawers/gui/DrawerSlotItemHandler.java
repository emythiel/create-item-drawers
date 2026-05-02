package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DrawerSlotItemHandler extends SlotItemHandler {

    public DrawerSlotItemHandler(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
    }

    public int getSlotIndex() {
        return index;
    }

    @Override
    public int getMaxStackSize() {
        ItemStack stored = getItem();
        return stored.isEmpty() ? 64 : stored.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return getItemHandler().getSlotLimit(index);
    }

    @Override @NotNull
    public ItemStack remove(int amount) {
        ItemStack stored = getItemHandler().getStackInSlot(index);
        if (stored.isEmpty()) return ItemStack.EMPTY;

        int naturalMax = stored.getMaxStackSize();
        int currentCount = stored.getCount();

        int toExtract = amount;
        if (currentCount > naturalMax && currentCount > 0) {
            toExtract = Math.max(1, (int) Math.round((double) amount / currentCount * naturalMax));
        }

        return getItemHandler().extractItem(index, toExtract, false);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        DrawerItemHandler handler = (DrawerItemHandler) getItemHandler();
        DrawerSlot drawerSlot = handler.getDrawerSlot(index);
        return drawerSlot != null && drawerSlot.canAccept(stack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return;

        DrawerItemHandler handler = (DrawerItemHandler) getItemHandler();
        ItemStack current = getItem();

        int delta;
        if (current.isEmpty() || !ItemStack.isSameItemSameComponents(current, stack)) {
            delta = stack.getCount();
        } else {
            delta = stack.getCount() - current.getCount();
        }

        if (delta > 0) {
            handler.insertItemAsPlayer(index, stack.copyWithCount(delta), false);
        }

        setChanged();
    }
}
