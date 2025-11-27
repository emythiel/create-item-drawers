package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DrawerMenu extends MenuBase<DrawerBlockEntity> {

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, DrawerBlockEntity be) {
        super(type, id, inv, be);
    }

    public static DrawerMenu create (int id, Inventory inv, DrawerBlockEntity be) {
        return new DrawerMenu(ModMenus.DRAWER_MENU.get(), id, inv, be);
    }

    @Override
    protected DrawerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        Level level = Minecraft.getInstance().level;
        return (DrawerBlockEntity) level.getBlockEntity(pos);
    }

    @Override
    protected void initAndReadInventory(DrawerBlockEntity be) {

    }

    @Override
    protected void addSlots() {

    }

    @Override
    protected void saveData(DrawerBlockEntity be) {
        be.setChangedAndSync();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }
}
