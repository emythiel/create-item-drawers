package dev.emythiel.createitemdrawers.storage;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public DrawerStorage getStorage() {
        return storage;
    }

    @Nullable
    public DrawerSlot getDrawerSlot(int slot) {
        if (slot < 0 || slot >= storage.getSlotCount()) return null;
        return storage.getSlot(slot);
    }

    /**
     * Playing insertion - used by contraptions to bypass the automation restriction,
     * so players can insert items into empty locked slots on contraptions.
     */
    @NotNull
    public ItemStack insertItemAsPlayer(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (slot < 0 || slot >= storage.getSlotCount())
            return stack;

        DrawerSlot s = storage.getSlot(slot);
        if (!s.canAccept(stack))
            return stack;

        ItemStack remaining = storage.insert(slot, stack, simulate);

        if (!simulate && remaining.getCount() != stack.getCount())
            onChange.run();

        return remaining;
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
    public int getStackLimit(int slot, @NotNull ItemStack stack) {
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

    /**
     * Replaces the contents of a drawer slot.
     * <p>
     * A drawer keeps a single template stack next to a separate count, so the slot
     * cannot simply store the given stack. The stack is normalised to a template of
     * size one and the count is clamped to the slot capacity. Stacks the slot would
     * refuse through {@link #insertItem} (locked templates, a different item, drawer
     * blocks themselves) are ignored, so a forced write cannot corrupt the drawer.
     */
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot < 0 || slot >= storage.getSlotCount())
            throw new IndexOutOfBoundsException(
                "Slot " + slot + " is out of bounds for a drawer with " + storage.getSlotCount() + " slots");

        DrawerSlot s = storage.getSlot(slot);

        if (stack.isEmpty()) {
            boolean wasEmpty = s.getStoredItem().isEmpty() && s.getCount() <= 0;

            s.setCount(0);
            if (!s.isLockMode())
                s.setStoredItem(ItemStack.EMPTY);

            if (!wasEmpty)
                onChange.run();
            return;
        }

        if (!s.canAccept(stack))
            return;

        // never store a non-empty template with a count of zero
        int newCount = Math.max(1, Math.min(stack.getCount(), storage.getCapacity(slot, stack)));
        ItemStack newTemplate = stack.copyWithCount(1);

        if (s.getCount() == newCount && ItemStack.isSameItemSameComponents(s.getStoredItem(), newTemplate))
            return;

        s.setStoredItem(newTemplate);
        s.setCount(newCount);
        onChange.run();
    }
}
