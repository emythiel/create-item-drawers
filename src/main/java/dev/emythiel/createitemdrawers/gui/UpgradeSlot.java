package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UpgradeSlot extends Slot {

    private final DrawerStorageBlockEntity be;

    public UpgradeSlot(DrawerStorageBlockEntity be, int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
        this.be = be;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof CapacityUpgradeItem;
    }

    @Override @NotNull
    public ItemStack getItem() {
        return be.getUpgrade();
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        be.setUpgrade(stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return true;
    }

    @Override @NotNull
    public ItemStack remove(int amount) {
        ItemStack item = be.getUpgrade();
        be.setUpgrade(ItemStack.EMPTY);
        return item;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
