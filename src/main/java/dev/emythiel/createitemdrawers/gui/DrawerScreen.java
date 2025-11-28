package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.gui.widgets.ToggleButton;
import dev.emythiel.createitemdrawers.network.DrawerConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {

    // Inventory slot widget (x, y, size)
    private static final int INV_SLOT_WIDGET_X = 238;
    private static final int INV_SLOT_WIDGET_Y = 0;
    private static final int INV_SLOT_WIDGET_SIZE = 18;
    // Toggle widget (x, y, width, heigh)
    private static final int TOGGLE_OFF_X = 241;
    private static final int TOGGLE_OFF_Y = 20;
    private static final int TOGGLE_ON_X = 241;
    private static final int TOGGLE_ON_Y = 28;
    private static final int TOGGLE_W = 12;
    private static final int TOGGLE_H = 7;
    // Render mode text
    private static final Component RENDER_MODE_LABEL =
        Component.translatable("gui.create_item_drawers.render_mode");
    private static final Component[] RENDER_OPTIONS = new Component[] {
        Component.translatable("gui.create_item_drawers.render_all"),
        Component.translatable("gui.create_item_drawers.render_item"),
        Component.translatable("gui.create_item_drawers.render_none")
    };

    private static final ResourceLocation TEXTURE =
        CreateItemDrawers.asResource("textures/gui/drawer.png");

    public DrawerScreen(DrawerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);

        this.imageWidth = 176;
        this.imageHeight = 213;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        drawSlotBackgrounds(graphics);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Drawer Title text
        graphics.drawString(this.font, this.title, 8, 4, 0x404040, false);

        // Player inventory title
        graphics.drawString(this.font, playerInventoryTitle, 8, 119, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        DrawerBlockEntity be = menu.contentHolder;

        IconButton closeMenuBtn =
            new IconButton(leftPos + 148, topPos + 85, AllIcons.I_CONFIRM);
        closeMenuBtn.withCallback(this::onClose);
        addRenderableWidget(closeMenuBtn);

        // Render scroll widget
        Label renderLabel = new Label(leftPos + 25, topPos + 90, Component.empty()).withShadow();

        ScrollInput renderScroll = new SelectionScrollInput(
            leftPos + 17, topPos + 85, 109, 18
        )
            .forOptions(List.of(RENDER_OPTIONS))
            .titled(RENDER_MODE_LABEL.plainCopy())
            .setState(be.getRenderMode())
            .writingTo(renderLabel)
            .calling(i -> {
                be.applyRenderMode(i);
                sendRenderModePacket(be.getBlockPos(), i);
            });

        addRenderableWidget(renderLabel);
        addRenderableWidget(renderScroll);

        // Per slot void/lock toggles
        int slots = be.getStorage().getSlotCount();
        for (int i = 0; i < slots; i++) {
            int sx = leftPos + 88 + ((i % 2) * 20) - 10;
            int sy = topPos + 50 + ((i / 2) * 20);

            int slotIndex = i;

            addRenderableWidget(new ToggleButton(
                sx, sy,
                TEXTURE,
                TOGGLE_OFF_X, TOGGLE_OFF_Y,
                TOGGLE_ON_X, TOGGLE_ON_Y,
                TOGGLE_W, TOGGLE_H,
                () -> be.getStorage().getSlot(slotIndex).isLockMode(),
                newVal -> {
                    be.getStorage().getSlot(slotIndex).setLockMode(newVal);
                    sendTogglePacket(be.getBlockPos(), "slotLock_" + slotIndex, newVal);
                }
            ));

        }


    }

    private void drawSlotBackgrounds(GuiGraphics graphics) {
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof ReadOnlySlotItemHandler)) continue;

            int x = leftPos + slot.x - 1;
            int y = topPos + slot.y - 1;

            graphics.blit(
                TEXTURE,
                x, y,
                INV_SLOT_WIDGET_X, INV_SLOT_WIDGET_Y,
                INV_SLOT_WIDGET_SIZE, INV_SLOT_WIDGET_SIZE
            );
        }
    }

    private void renderToggleWidget(GuiGraphics graphics, int x, int y) {

    }

    private static final int SLOT_SIZE = 18;
    private int slotX(int col) { return leftPos + 44 + col * SLOT_SIZE; }
    private int slotY(int row) { return topPos + 26 + row * SLOT_SIZE; }

    private void sendTogglePacket(BlockPos pos, String key, boolean value) {
        //
    }

    private void sendRenderModePacket(BlockPos pos, int mode) {
        PacketDistributor.sendToServer(new DrawerConfigPacket(pos, mode));
    }


}
