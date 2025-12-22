package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DrawerMenu extends MenuBase<DrawerStorageBlockEntity> {

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, DrawerStorageBlockEntity be) {
        super(type, id, inv, be);
    }

    public static DrawerMenu create (int id, Inventory inv, DrawerStorageBlockEntity be) {
        return new DrawerMenu(ModMenus.DRAWER_MENU.get(), id, inv, be);
    }

    @Override
    protected DrawerStorageBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        Level level = Minecraft.getInstance().level;
        return (DrawerStorageBlockEntity) level.getBlockEntity(pos);
    }

    @Override
    protected void initAndReadInventory(DrawerStorageBlockEntity be) {

    }

    @Override
    protected void addSlots() {
        // Player inventory (9x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                    this.player.getInventory(),
                    col + row * 9 + 9,
                    8 + col * 18,
                    131 + row * 18
                ));
            }
        }

        // Hotbar (9)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(
                this.player.getInventory(),
                i,
                8 + i * 18,
                189
            ));
        }

        // Drawer slots
        DrawerStorageBlockEntity be = contentHolder;
        int count = be.getStorage().getSlotCount();

        if (count == 1) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 110, 39));
        }
        else if (count == 2) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 110, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 1, 110, 51));
        }
        else if (count == 4) {
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 0, 98, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 1, 122, 27));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 2, 98, 51));
            this.addSlot(new ReadOnlySlotItemHandler(be.getLocalHandler(), 3, 122, 51));
        }

        // Upgrade slot
        this.addSlot(new UpgradeSlot(be, 24, 39));
    }

    @Override
    protected void saveData(DrawerStorageBlockEntity be) {
        be.setChangedAndSync();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
}
