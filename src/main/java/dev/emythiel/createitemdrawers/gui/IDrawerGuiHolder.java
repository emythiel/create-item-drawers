package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.storage.DrawerItemHandler;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IDrawerGuiHolder {

    int getSlotCount();

    @Nullable
    DrawerSlot getDrawerSlot(int slot);

    DrawerItemHandler getLocalHandler();

    ItemStack getUpgrade();
    void setUpgrade(ItemStack stack);

    boolean getRenderItems();
    void setRenderItems(boolean v);

    boolean getRenderCounts();
    void setRenderCounts(boolean v);

    boolean getRenderIcons();
    void setRenderIcons(boolean v);
}

