package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DrawerMenu extends AbstractDrawerMenu<DrawerStorageBlockEntity> {

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        super(type, id, inv, buf);
    }

    public DrawerMenu(MenuType<?> type, int id, Inventory inv, DrawerStorageBlockEntity be) {
        super(type, id, inv, be);
    }

    public static DrawerMenu create(int id, Inventory inv, DrawerStorageBlockEntity be) {
        return new DrawerMenu(ModMenuTypes.DRAWER_MENU.get(), id, inv, be);
    }

    @Override
    protected DrawerStorageBlockEntity createOnClient(RegistryFriendlyByteBuf buf) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockEntity be = level.getBlockEntity(buf.readBlockPos());
        return (be instanceof DrawerStorageBlockEntity drawerBE) ? drawerBE : null;
    }
}
