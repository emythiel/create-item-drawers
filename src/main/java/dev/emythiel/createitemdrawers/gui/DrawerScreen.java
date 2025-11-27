package dev.emythiel.createitemdrawers.gui;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(
            CreateItemDrawers.MODID,
            "textures/gui/drawer.png"
        );

    public DrawerScreen(DrawerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);

        this.imageWidth = 181;
        this.imageHeight = 221;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight
        );
    }
}
