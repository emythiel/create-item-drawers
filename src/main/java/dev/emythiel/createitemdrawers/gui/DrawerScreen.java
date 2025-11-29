package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.gui.widgets.ToggleButton;
import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {

    // Inventory slot widget (x, y, size)
    private static final int INV_SLOT_WIDGET_X = 238;
    private static final int INV_SLOT_WIDGET_Y = 0;
    private static final int INV_SLOT_WIDGET_SIZE = 18;
    // Toggle widget (x, y, width, height)
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        renderTooltip(graphics, mouseX, mouseY);
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

        // Upgrade slot title
        {
            float scale = 0.75f;
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1f);
            int drawX = (int)(17 / scale);
            int drawY = (int)(26 / scale);
            graphics.drawString(
                this.font,
                Component.translatable("gui.create_item_drawers.upgrade_slot"),
                drawX, drawY,
                0x404040,
                false
            );

            graphics.pose().popPose();
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlotContents(GuiGraphics graphics, @NotNull ItemStack stack, Slot slot, String countString) {
        int hash = slot.x + slot.y * this.imageWidth;

        graphics.renderItem(stack, slot.x, slot.y, hash);

        if (!(slot instanceof ReadOnlySlotItemHandler)) {
            graphics.renderItemDecorations(this.font, stack, slot.x, slot.y, countString);
            return;
        }

        int count = stack.getCount();

        if (count <= 1) return;

        String s = (count > 9999) ? (count / 1000) + "k" : String.valueOf(count);

        // Position
        float scale = 0.60f;
        int centerX = slot.x + 8;
        int textWidth = this.font.width(s);

        graphics.pose().pushPose();

        graphics.pose().translate(0, 0, 200); // Push text in front of item

        // Re-scale
        graphics.pose().scale(scale, scale, 1);

        // Convert slot coordinates to scaled space
        float inv = 1f / scale;

        int drawX = (int)((centerX - textWidth * scale / 2) * inv);
        int drawY = (int)((slot.y + 16 + 2) * inv);

        graphics.drawString(this.font, s, drawX, drawY, 0xDDDDDD, true);

        graphics.pose().popPose();
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
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof ReadOnlySlotItemHandler roSlot)) continue;
            int slotCount = be.getStorage().getSlotCount();

            int slotIndex = roSlot.getSlotIndex();

            int sx = leftPos + slot.x;
            int sy = topPos + slot.y;

            int toggleX = sx - TOGGLE_W - 2;

            // If quad drawer, move widgets for slot 1 and 3 to right
            if (slotCount == 4 && slotIndex % 2 == 1) {
                toggleX = sx + INV_SLOT_WIDGET_SIZE;
            }

            // Vertical offset - lock top, void bottom
            int lockY = sy;
            int voidY = lockY + TOGGLE_H + 1;

            // Lock mode
            addRenderableWidget(new ToggleButton(
                toggleX, lockY,
                TEXTURE,
                TOGGLE_OFF_X, TOGGLE_OFF_Y,
                TOGGLE_ON_X, TOGGLE_ON_Y,
                TOGGLE_W, TOGGLE_H,
                () -> be.getStorage().getSlot(slotIndex).isLockMode(),
                newVal -> {
                    be.getStorage().getSlot(slotIndex).setLockMode(newVal);
                    sendTogglePacket(be.getBlockPos(), slotIndex, "lock", newVal);
                }
            ).withTooltip(() ->
                be.getStorage().getSlot(slotIndex).isLockMode()
                    ? Component.translatable("gui.create_item_drawers.tooltip.unlock_slot")
                    : Component.translatable("gui.create_item_drawers.tooltip.lock_slot")
            ));

            // Void mode
            addRenderableWidget(new ToggleButton(
                toggleX, voidY,
                TEXTURE,
                TOGGLE_OFF_X, TOGGLE_OFF_Y,
                TOGGLE_ON_X, TOGGLE_ON_Y,
                TOGGLE_W, TOGGLE_H,
                () -> be.getStorage().getSlot(slotIndex).isVoidMode(),
                newVal -> {
                    be.getStorage().getSlot(slotIndex).setVoidMode(newVal);
                    sendTogglePacket(be.getBlockPos(), slotIndex, "void", newVal);
                }
            ).withTooltip(() ->
                be.getStorage().getSlot(slotIndex).isVoidMode()
                    ? Component.translatable("gui.create_item_drawers.tooltip.disable_void")
                    : Component.translatable("gui.create_item_drawers.tooltip.enable_void")
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

    private void sendTogglePacket(BlockPos pos, int slot, String type, boolean value) {
        boolean isLock = type.equals("lock");
        PacketDistributor.sendToServer(new SlotTogglePacket(pos, slot, isLock, value));
    }

    private void sendRenderModePacket(BlockPos pos, int mode) {
        PacketDistributor.sendToServer(new RenderPacket(pos, mode));
    }


}
