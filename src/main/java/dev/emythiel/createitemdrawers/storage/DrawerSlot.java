package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.config.ServerConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * DrawerSlot represents a single storage compartment inside a drawer.
 * Responsibilities:
 *   ✔ Track the stored item type (ItemStack)
 *   ✔ Track the total item count (can exceed 64)
 *   ✔ Handle per-slot filter rules
 *   ✔ Handle void-mode (destroy overflow items)
 *   ✔ Perform insertion and extraction logic
 *   ✔ Save/load its own data to NBT
 */
public class DrawerSlot {

    private ItemStack storedItem = ItemStack.EMPTY;
    private int count = 0;
    private boolean lockMode = false;
    private boolean voidMode = false;

    public DrawerSlot() {}

    /** A slot is “empty” if the stored item is empty OR count == 0. */
    public boolean isEmpty() {
        return storedItem.isEmpty();
    }


    /* Getters and Setters */

    public ItemStack getStoredItem() { return storedItem; }

    public int getCount() { return count; }

    public boolean isVoidMode() { return voidMode; }
    public void setVoidMode(boolean v) { this.voidMode = v; }

    public boolean isLockMode() { return lockMode; }
    public void setLockMode(boolean v) { this.lockMode = v;}

    public void unlock() {
        lockMode = false;
        if (count <= 0) {
            storedItem = ItemStack.EMPTY;
        }
    }


    /* Item matching logic */

    /**
     * True if both stacks represent the same item + NBT (like damage, enchantments, etc.)
     */
    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(storedItem, stack);
    }

    /**
     * Determines whether this slot *can accept* the given stack according to rules:
     *   ✔ filter blocks items that do not match
     *   ✔ empty slot can accept any item (unless filter blocks it)
     *   ✔ non-empty slot must match stored item
     */
    public boolean canAccept(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (ServerConfig.isBlacklisted(id) || stack.isEmpty())
            return false;

        // Locked, slot not empty -> must match template
        if (lockMode && !isEmpty())
            return matches(stack);

        // Unlocked, empty -> accept anything
        if (isEmpty())
            return true;

        // Unlocked, not empty -> must match stored item
        return matches(stack);
    }


    /* Insertion logic */

    /**
     * Inserts items into this slot.
     *
     * @param stack     incoming items
     * @param capacity  max number allowed in this slot (capacity is calculated elsewhere)
     * @param simulate  if true, do NOT modify the slot
     * @return          leftover stack (empty = all items inserted)
     */
    public ItemStack insert(ItemStack stack, int capacity, boolean simulate) {
        if (!canAccept(stack))
            return stack;

        int space = capacity - count;
        if (space <= 0) {
            return voidMode ? ItemStack.EMPTY : stack;
        }

        int toAdd = Math.min(stack.getCount(), space);

        if (!simulate) {
            if (isEmpty()) {
                storedItem = stack.copyWithCount(1);
            }
            count += toAdd;
        }

        if (toAdd == stack.getCount() || voidMode)
            return ItemStack.EMPTY;

        ItemStack leftover = stack.copy();
        leftover.setCount(stack.getCount() - toAdd);
        return leftover;
    }


    /* Extraction logic */

    /**
     * Extract items from this slot.
     *
     * @param amount    how many items the player wants
     * @param simulate  if true, do NOT change stored values
     * @return          items withdrawn (can be more than 64!)
     */
    public ItemStack extract(int amount, boolean simulate) {
        if (isEmpty())
            return ItemStack.EMPTY;

        int take = Math.min(amount, count);
        ItemStack out = storedItem.copyWithCount(take);

        if (!simulate) {
            count -= take;
            if (count <= 0) {
                if (!lockMode)
                    storedItem = ItemStack.EMPTY; // Keep stored item as template if locked
                count = 0;
            }
        }

        return out;
    }


    /* NBT serialization */

    public void save(CompoundTag tag, HolderLookup.Provider provider) {
        if (!storedItem.isEmpty()) {
            tag.put("Item", storedItem.save(provider));
        }
        tag.putInt("Count", count);
        tag.putBoolean("Locked", lockMode);
        tag.putBoolean("Void", voidMode);
    }

    public void load(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("Item")) {
            storedItem = ItemStack.parseOptional(provider, tag.getCompound("Item"));
        } else {
            storedItem = ItemStack.EMPTY;
        }
        count = tag.getInt("Count");
        lockMode = tag.getBoolean("Locked");
        voidMode = tag.getBoolean("Void");
    }
}
