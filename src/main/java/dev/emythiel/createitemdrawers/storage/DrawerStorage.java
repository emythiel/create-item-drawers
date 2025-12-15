package dev.emythiel.createitemdrawers.storage;

import dev.emythiel.createitemdrawers.config.ServerConfig;
import net.minecraft.world.item.ItemStack;

/**
 * DrawerStorage manages multiple DrawerSlot objects inside a drawer.
 * Each drawer block variant (1-slot, 2-slot, 4-slot) creates 1 instance of this class,
 * and this instance holds the per-slot data.
 * Responsibilities:
 *   ✔ Manage N slots
 *   ✔ Calculate capacity for a slot
 *   ✔ Redirect inserton/extraction to specific DrawerSlot instances
 *   ✔ Apply global "upgrade multiplier" (affects ALL slots)
 */
public class DrawerStorage {
    private final DrawerSlot[] slots;
    private int upgradeMultiplier = 1;

    /**
     * Initializes N drawer slots.
     *
     * @param slotCount      number of slots (1, 2, or 4 depending on drawer type)
     */
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

    /* Upgrades */
    public void setUpgradeMultiplier(int mult) {
        this.upgradeMultiplier = Math.max(1, mult);
    }

    /* Capacity calculation */

    /**
     * Calculate how many items this slot can store.
     * Logic:
     *   - If slot already contains an item, use that for stack size.
     *   - Otherwise, use the item being inserted.
     *   - If still empty, use fallback "stack size = 64".
     * Example:
     *   quad drawer = baseMultiplier = 8
     *   iron ingots = max stack size = 64
     *   upgradeMultiplier = 1
     *   capacity = 64 * 8 * 1 = 512 items
     */
    public int getCapacity(int slotIndex, ItemStack itemForCapacity) {
        DrawerSlot slot = slots[slotIndex];
        int baseMultiplier = switch(getSlotCount()) {
            case 1 -> ServerConfig.SINGLE_CAPACITY.get();
            case 2 -> ServerConfig.DOUBLE_CAPACITY.get();
            case 4 -> ServerConfig.QUAD_CAPACITY.get();
            default -> 32;
        };

        ItemStack item = !slot.getStoredItem().isEmpty()
            ? slot.getStoredItem()
            : itemForCapacity;

        if (item.isEmpty())
            return 64 * baseMultiplier * upgradeMultiplier;  // fallback

        return baseMultiplier * item.getMaxStackSize() * upgradeMultiplier;
    }


    /* Insertion / Extraction */

    /**
     * Insert an ItemStack into a given slot index.
     * Fully delegates the actual logic to DrawerSlot.insert().
     * @return leftover items (empty means everything inserted)
     */
    public ItemStack insert(int slotIndex, ItemStack stack, boolean simulate) {
        DrawerSlot slot = slots[slotIndex];

        int capacity = getCapacity(slotIndex, stack);
        if (capacity < slot.getCount())
            return stack;

        return slot.insert(stack, capacity, simulate);
    }

    /**
     * Extract items from slot.
     * Delegates to DrawerSlot.extract().
     */
    public ItemStack extract(int slotIndex, int amount, boolean simulate) {
        DrawerSlot slot = slots[slotIndex];

        int capacity = getCapacity(slotIndex, slot.getStoredItem());
        if (capacity < slot.getCount())
            return ItemStack.EMPTY;

        return slots[slotIndex].extract(amount, simulate);
    }
}
