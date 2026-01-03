package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.config.ServerConfig;
import dev.emythiel.createitemdrawers.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DrawerSlot {

    private ItemStack storedItem = ItemStack.EMPTY;
    private int count = 0;
    private boolean lockMode = false;
    private boolean voidMode = false;

    public DrawerSlot() {}


    /* Getters & Setters */

    public ItemStack getStoredItem() {
        return storedItem;
    }
    public void setStoredItem(ItemStack stack) {
        storedItem = stack;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int c) {
        count = c;
    }

    public boolean isLockMode() {
        return lockMode;
    }
    public void setLockMode(boolean v) {
        this.lockMode = v;

        // if unlocked AND empty -> clear template
        if (!v && count <= 0) {
            storedItem = ItemStack.EMPTY;
        }
    }

    public boolean isVoidMode() {
        return voidMode;
    }
    public void setVoidMode(boolean v) {
        this.voidMode = v;
    }


    /* Item matching logic */

    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(storedItem, stack);
    }

    public boolean canAccept(ItemStack stack) {
        if (stack.is(ModTags.Items.DRAWERS) || stack.isEmpty())
            return false;

        // Locked, slot not empty -> must match template
        if (lockMode && !getStoredItem().isEmpty())
            return matches(stack);

        // Unlocked, empty -> accept anything
        if (getStoredItem().isEmpty())
            return true;

        // Unlocked, not empty -> must match stored item
        return matches(stack);
    }


    /* Insertion logic */

    public ItemStack insert(ItemStack stack, int capacity, boolean simulate) {
        if (!canAccept(stack))
            return stack;

        int space = capacity - count;
        if (space <= 0) {
            return voidMode ? ItemStack.EMPTY : stack;
        }

        int toAdd = Math.min(stack.getCount(), space);

        if (!simulate) {
            if (getStoredItem().isEmpty()) {
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

    public ItemStack extract(int amount, boolean simulate) {
        if (getStoredItem().isEmpty())
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
