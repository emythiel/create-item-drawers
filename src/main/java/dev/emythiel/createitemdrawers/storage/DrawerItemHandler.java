package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
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

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= storage.getSlotCount())
            return ItemStack.EMPTY;

        DrawerSlot s = storage.getSlot(slot);
        if (s.isEmpty())
            return ItemStack.EMPTY;

        ItemStack base = s.getStoredItem();
        if (base.isEmpty())
            return ItemStack.EMPTY;

        ItemStack copy = base.copy();
        copy.setCount(s.getCount());
        return copy;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (slot > 3)
            return ItemStack.EMPTY;

        ItemStack remaining = stack;
        int slotCount = storage.getSlotCount();

        // Check and insert into a slot containing same item first
        for (int i = 0; i < slotCount && !remaining.isEmpty(); i++) {
            DrawerSlot s = storage.getSlot(i);

            if (s.isEmpty())
                continue;

            if (!s.canAccept(remaining))
                continue;

            remaining = storage.insert(i, remaining, simulate);
        }

        // Insert into first available slot
        if (!remaining.isEmpty() && slot >= 0 && slot < slotCount) {
            DrawerSlot s = storage.getSlot(slot);

            if (s.isEmpty() && s.isLockMode())
                return remaining;

            if (s.canAccept(remaining))
                remaining = storage.insert(slot, remaining, simulate);
        }

        /* TODO: Fallback loop through every slot
            should not be needed when calling slot directly above, but test anyway.
        if (!remaining.isEmpty()) {
            for (int i = 0; i < slotCount && !remaining.isEmpty(); i++) {
                DrawerSlot s = storage().getSlot(i);
                if (!s.isEmpty())
                    continue;

                if (s.isEmpty() && s.isLockMode())
                    return remaining;

                if (!s.canAccept(remaining))
                    continue;

                remaining = storage().insert(i, remaining, simulate);
            }
        }*/

        if (!simulate && remaining.getCount() != stack.getCount())
            onChange.run();

        return remaining;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0)
            return ItemStack.EMPTY;

        if (slot < 0 || slot >= storage.getSlotCount())
            return ItemStack.EMPTY;

        DrawerSlot s = storage.getSlot(slot);
        if (s.isEmpty())
            return ItemStack.EMPTY;

        ItemStack extracted = storage.extract(slot, amount, simulate);

        if (!simulate && !extracted.isEmpty()) {
            onChange.run();
        }

        return extracted;
    }

    @Override
    public int getStackLimit(int slot, ItemStack stack) {
        if (slot >= 0 && slot < 4) {
            return storage.getCapacity(slot, stack);
        }
        return super.getStackLimit(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot >= 0 && slot < 4) {
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

        return storage.getSlot(slot).canAccept(stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {

    }
}
