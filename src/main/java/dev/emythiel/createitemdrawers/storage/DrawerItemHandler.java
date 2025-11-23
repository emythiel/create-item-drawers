package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class DrawerItemHandler implements IItemHandler {

    private final DrawerBlockEntity drawer;

    public DrawerItemHandler(DrawerBlockEntity drawer) {
        this.drawer = drawer;
    }

    private DrawerStorage storage() {
        return drawer.getStorage();
    }

    @Override
    public int getSlots() {
        return storage().getSlotCount();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        DrawerSlot s = storage().getSlot(slot);
        if (s.isEmpty())
            return ItemStack.EMPTY;

        ItemStack base = s.getStoredItem();
        if (base.isEmpty())
            return ItemStack.EMPTY;

        ItemStack copy = base.copy();
        int count = Math.min(base.getMaxStackSize(), s.getCount());
        copy.setCount(count);
        return copy;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack remaining = stack;

        // Insert into a slot with same item (if possible) first
        for (int i = 0; i < storage().getSlotCount(); i++) {
            DrawerSlot s = storage().getSlot(i);

            if (s.isEmpty())
                continue;

            if (!ItemStack.isSameItemSameComponents(s.getStoredItem(), remaining))
                continue;

            remaining = storage().insert(i, remaining, simulate);
            if (remaining.isEmpty())
                break;
        }

        if (remaining.isEmpty()) {
            if (!simulate)
                drawer.setChangedAndSync();
            return ItemStack.EMPTY;
        }

        // Insert into first available slot
        for (int i = 0; i < storage().getSlotCount(); i++) {
            DrawerSlot s = storage().getSlot(i);

            if (!s.isEmpty())
                continue;

            remaining = storage().insert(i, remaining, simulate);
            if (remaining.isEmpty())
                break;
        }

        if (!simulate && remaining.getCount() != stack.getCount()) {
            drawer.setChangedAndSync();
        }

        return remaining;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0)
            return ItemStack.EMPTY;

        for (int i = 0; i < storage().getSlotCount(); i++) {
            DrawerSlot s = storage().getSlot(i);
            if (s.isEmpty())
                continue;

            ItemStack out = storage().extract(i, amount, simulate);
            if (!out.isEmpty()) {
                if (!simulate)
                    drawer.setChangedAndSync();
                return out;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (stack.isEmpty())
            return false;

        for (int i = 0; i < storage().getSlotCount(); i++) {
            if (storage().getSlot(i).canAccept(stack))
                return true;
        }
        return false;
    }
}
