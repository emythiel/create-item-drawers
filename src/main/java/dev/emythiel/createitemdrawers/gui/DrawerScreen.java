package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class DrawerScreen extends AbstractDrawerScreen<DrawerMenu> {

    public DrawerScreen(DrawerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void sendTogglePacket(int slot, SlotTogglePacket.ToggleMode mode, boolean value) {
        PacketDistributor.sendToServer(
            new SlotTogglePacket(menu.contentHolder.getBlockPos(), slot, mode, value)
        );
    }
}
