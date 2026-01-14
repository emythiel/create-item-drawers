package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.registry.ModConfigs;
import net.minecraft.world.item.ItemStack;

public class DrawerStorage {
    private final DrawerSlot[] slots;
    private int upgradeMultiplier = 1;

    public DrawerStorage(int slotCount) {
        this.slots = new DrawerSlot[slotCount];
        for (int i = 0; i < slotCount; i++)
            this.slots[i] = new DrawerSlot();
    }


    /* Basic access */

    public DrawerSlot getSlot(int i) {
        return slots[i];
    }
    public int getSlotCount() {
        return slots.length;
    }

    public void setUpgradeMultiplier(int mult) {
        this.upgradeMultiplier = Math.max(1, mult);
    }


    /* Capacity calculation */

    public int getCapacity(int slotIndex, ItemStack itemForCapacity) {
        DrawerSlot slot = slots[slotIndex];
        int baseMultiplier = switch(getSlotCount()) {
            case 1 -> ModConfigs.server().storageCapacitySingle.get();
            case 2 -> ModConfigs.server().storageCapacityDouble.get();
            case 4 -> ModConfigs.server().storageCapacityQuad.get();
            default -> 32;
        };

        ItemStack item = !slot.getStoredItem().isEmpty()
            ? slot.getStoredItem()
            : itemForCapacity;

        if (item.isEmpty())
            return 64 * baseMultiplier * upgradeMultiplier;  // fallback

        return baseMultiplier * item.getMaxStackSize() * upgradeMultiplier;
    }


    /* Combined slot capacity and counts for Create Threshold Switches */

    public int getCombinedSlotCapacity() {
        int combinedCapacity = 0;
        for (int i = 0; i < getSlotCount(); i++) {
            combinedCapacity += getCapacity(i, getSlot(i).getStoredItem());
        }
        return combinedCapacity;
    }

    public int getCombinedSlotCount() {
        int combinedCount = 0;
        for (int i = 0; i < getSlotCount(); i++) {
            combinedCount += getSlot(i).getCount();
        }
        return combinedCount;
    }


    /* Insertion / Extraction */

    public ItemStack insert(int slotIndex, ItemStack stack, boolean simulate) {
        DrawerSlot slot = slots[slotIndex];

        int capacity = getCapacity(slotIndex, stack);
        if (capacity < slot.getCount())
            return stack;

        return slot.insert(stack, capacity, simulate);
    }

    public ItemStack extract(int slotIndex, int amount, boolean simulate) {
        DrawerSlot slot = slots[slotIndex];

        int capacity = getCapacity(slotIndex, slot.getStoredItem());
        if (capacity < slot.getCount())
            return ItemStack.EMPTY;

        return slots[slotIndex].extract(amount, simulate);
    }
}
