package dev.emythiel.createitemdrawers.storage;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DrawerItemHandler extends ItemStackHandler {

    private final DrawerStorage storage;
    private Runnable onChange = () -> {};

    public DrawerItemHandler(DrawerStorage storage) {
        this.storage = storage;
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    @Override
    public int getSlots() {
        return storage.getSlotCount();
    }

    @Override @NotNull
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= storage.getSlotCount())
            return ItemStack.EMPTY;

        DrawerSlot s = storage.getSlot(slot);

        if (s.getCount() <= 0)
            return ItemStack.EMPTY;

        ItemStack copy = s.getStoredItem().copy();
        copy.setCount(s.getCount());
        return copy;
    }

    @Override @NotNull
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (slot < 0 || slot >= storage.getSlotCount())
            return stack;

        if (!isItemValid(slot, stack))
            return stack;

        ItemStack remaining = storage.insert(slot, stack, simulate);

        if (!simulate && remaining.getCount() != stack.getCount())
            onChange.run();

        return remaining;
    }

    @Override @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0)
            return ItemStack.EMPTY;

        if (slot < 0 || slot >= storage.getSlotCount())
            return ItemStack.EMPTY;

        DrawerSlot s = storage.getSlot(slot);
        if (s.getStoredItem().isEmpty())
            return ItemStack.EMPTY;

        ItemStack extracted = storage.extract(slot, amount, simulate);

        if (!simulate && !extracted.isEmpty()) {
            onChange.run();
        }

        return extracted;
    }

    @Override
    public int getStackLimit(int slot, ItemStack stack) {
        if (slot < storage.getSlotCount()) {
            return storage.getCapacity(slot, stack);
        }
        return super.getStackLimit(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < storage.getSlotCount()) {
            return storage.getCapacity(slot, ItemStack.EMPTY);
        }
        return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (slot < 0 || slot >= storage.getSlotCount())
            return false;

        DrawerSlot s = storage.getSlot(slot);

        if (s.isLockMode() && s.getStoredItem().isEmpty())
            return false;

        return s.canAccept(stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {

    }
}
