package dev.emythiel.createitemdrawers.contraption;

import dev.emythiel.createitemdrawers.gui.AbstractDrawerScreen;
import dev.emythiel.createitemdrawers.network.ContraptionSlotTogglePacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket.ToggleMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ContraptionDrawerScreen extends AbstractDrawerScreen<ContraptionDrawerMenu> {

    public ContraptionDrawerScreen(ContraptionDrawerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void sendTogglePacket(int slot, ToggleMode mode, boolean value) {
        PacketDistributor.sendToServer(new ContraptionSlotTogglePacket(
            menu.contraptionEntityId, menu.localPos, slot, mode, value
        ));
    }

}
