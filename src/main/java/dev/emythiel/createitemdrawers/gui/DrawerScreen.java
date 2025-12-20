package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.gui.widgets.ToggleButton;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.createmod.catnip.lang.FontHelper;
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

import java.util.ArrayList;
import java.util.List;

import static dev.emythiel.createitemdrawers.network.SlotTogglePacket.ToggleMode.*;
import static net.createmod.catnip.lang.FontHelper.styleFromColor;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {

    // Inventory slot widget (x, y, size)
    private static final int INV_SLOT_WIDGET_X = 238;
    private static final int INV_SLOT_WIDGET_Y = 0;
    private static final int INV_SLOT_WIDGET_SIZE = 18;
    // Toggle widget (x, y, width, height)
    private static final int TOGGLE_OFF_X = 244;
    private static final int TOGGLE_OFF_Y = 36;
    private static final int TOGGLE_ON_X = 244;
    private static final int TOGGLE_ON_Y = 43;
    private static final int TOGGLE_W = 12;
    private static final int TOGGLE_H = 7;
    // Lock widget (x, y, width, height)
    private static final int LOCK_ON_X = 247;
    private static final int LOCK_ON_Y = 27;
    private static final int LOCK_OFF_X = 238;
    private static final int LOCK_OFF_Y = 27;
    // Void widget (x, y, width, height)
    private static final int VOID_ON_X = 247;
    private static final int VOID_ON_Y = 18;
    private static final int VOID_OFF_X = 238;
    private static final int VOID_OFF_Y = 18;
    // Lock/Void widget size (width, height)
    private static final int LOCK_VOID_W = 9;
    private static final int LOCK_VOID_H = 9;
    // Render mode text
    private static final Component RENDER_MODE_LABEL =
        CreateItemDrawerLang.translate("gui.render_mode").component();
    private static final Component[] RENDER_OPTIONS = new Component[] {
        CreateItemDrawerLang.translate("gui.render_all").component(),
        CreateItemDrawerLang.translate("gui.render_item").component(),
        CreateItemDrawerLang.translate("gui.render_none").component()
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
            float scale = 0.70f;
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1f);
            int drawX = (int)(17 / scale);
            int drawY = (int)(26 / scale);
            graphics.drawString(
                this.font,
                CreateItemDrawerLang.translate("gui.upgrade_slot").component(),
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
        boolean isTemplateItem = false;

        // If drawer slot and empty but locked, show template item
        if (slot instanceof ReadOnlySlotItemHandler ro) {
            DrawerBlockEntity be = menu.contentHolder;
            DrawerSlot drawerSlot = be.getStorage().getSlot(ro.getSlotIndex());

            // Check if slot is locked and has template, but actual count is 0
            if (drawerSlot.isLockMode() && !drawerSlot.getStoredItem().isEmpty() && drawerSlot.getCount() == 0) {
                stack = drawerSlot.getStoredItem();
                isTemplateItem = true;
            }
        }
        graphics.renderItem(stack, slot.x, slot.y, hash);
        // If template item, apply gray overlay on item and don't render count
        if (isTemplateItem) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 275);
            graphics.fill(
                slot.x, slot.y,
                slot.x + 16, slot.y + 16,
                0x80AAAAAA
            );
            graphics.pose().popPose();
            return;
        }

        if (!(slot instanceof ReadOnlySlotItemHandler)) {
            graphics.renderItemDecorations(this.font, stack, slot.x, slot.y, countString);
            return;
        }

        // Don't render count if empty
        if (stack.isEmpty())
            return;

        int count = stack.getCount();

        // If above 10.000, show count as 10k etc
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

        //int drawX = (int)((centerX - textWidth * scale / 2) * inv);
        int drawX = (int)((centerX - textWidth * scale / 2) * inv);
        int drawY = (int)((slot.y + 16 - 5) * inv);

        graphics.drawString(this.font, s, drawX, drawY, 0xFFFFFF/*0xDDDDDD*/, true);

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

        // Render settings
        addRenderableWidget(new ToggleButton(
            leftPos + 126, topPos + 82,
            TEXTURE,
            TOGGLE_OFF_X, TOGGLE_OFF_Y,
            TOGGLE_ON_X, TOGGLE_ON_Y,
            TOGGLE_W, TOGGLE_H,
            be::getRenderItems,
            newVal -> {
                be.setRenderItems(newVal);
                sendTogglePacket(be.getBlockPos(), 0, ITEMS, newVal);
            }
        ).withMultiLineTooltip(() -> {
            String headerKey = be.getRenderItems()
                ? "gui.tooltip.items_hide"
                : "gui.tooltip.items_show";
            return createFormattedTooltip(headerKey, "gui.tooltip.items_description");
        }));
        addRenderableWidget(new ToggleButton(
            leftPos + 126, topPos + 90,
            TEXTURE,
            TOGGLE_OFF_X, TOGGLE_OFF_Y,
            TOGGLE_ON_X, TOGGLE_ON_Y,
            TOGGLE_W, TOGGLE_H,
            be::getRenderCounts,
            newVal -> {
                be.setRenderCounts(newVal);
                sendTogglePacket(be.getBlockPos(), 0, COUNTS, newVal);
            }
        ).withMultiLineTooltip(() -> {
            String headerKey = be.getRenderCounts()
                ? "gui.tooltip.counts_hide"
                : "gui.tooltip.counts_show";
            return createFormattedTooltip(headerKey, "gui.tooltip.counts_description");
        }));
        addRenderableWidget(new ToggleButton(
            leftPos + 126, topPos + 98,
            TEXTURE,
            TOGGLE_OFF_X, TOGGLE_OFF_Y,
            TOGGLE_ON_X, TOGGLE_ON_Y,
            TOGGLE_W, TOGGLE_H,
            be::getRenderAdditional,
            newVal -> {
                be.setRenderAdditional(newVal);
                sendTogglePacket(be.getBlockPos(), 0, SETTINGS, newVal);
            }
        ).withMultiLineTooltip(() -> {
            String headerKey = be.getRenderAdditional()
                ? "gui.tooltip.settings_hide"
                : "gui.tooltip.settings_show";
            return createFormattedTooltip(headerKey, "gui.tooltip.settings_description");
        }));

        // Per slot void/lock toggles
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof ReadOnlySlotItemHandler roSlot)) continue;
            int slotCount = be.getStorage().getSlotCount();

            int slotIndex = roSlot.getSlotIndex();

            int sx = leftPos + slot.x;
            int sy = topPos + slot.y;

            int toggleX = sx - LOCK_VOID_W - 1;

            // If quad drawer, move widgets for slot 1 and 3 to right
            if (slotCount == 4 && slotIndex % 2 == 1) {
                toggleX = sx + INV_SLOT_WIDGET_SIZE - 1;
            }

            // Vertical offset - lock top, void bottom
            int lockY = sy - 1;
            int voidY = lockY + LOCK_VOID_H;

            // Lock mode
            addRenderableWidget(new ToggleButton(
                toggleX, lockY,
                TEXTURE,
                LOCK_OFF_X, LOCK_OFF_Y,
                LOCK_ON_X, LOCK_ON_Y,
                LOCK_VOID_W, LOCK_VOID_H,
                () -> be.getStorage().getSlot(slotIndex).isLockMode(),
                newVal -> {
                    be.getStorage().getSlot(slotIndex).setLockMode(newVal);
                    sendTogglePacket(be.getBlockPos(), slotIndex, LOCK, newVal);
                }
            ).withMultiLineTooltip(() -> {
                String headerKey = be.getStorage().getSlot(slotIndex).isLockMode()
                    ? "gui.tooltip.lock_disable"
                    : "gui.tooltip.lock_enable";
                return createFormattedTooltip(headerKey, "gui.tooltip.lock_description");
            }));

            // Void mode
            addRenderableWidget(new ToggleButton(
                toggleX, voidY,
                TEXTURE,
                VOID_OFF_X, VOID_OFF_Y,
                VOID_ON_X, VOID_ON_Y,
                LOCK_VOID_W, LOCK_VOID_H,
                () -> be.getStorage().getSlot(slotIndex).isVoidMode(),
                newVal -> {
                    be.getStorage().getSlot(slotIndex).setVoidMode(newVal);
                    sendTogglePacket(be.getBlockPos(), slotIndex, VOID, newVal);
                }
            ).withMultiLineTooltip(() -> {
                String headerKey = be.getStorage().getSlot(slotIndex).isVoidMode()
                    ? "gui.tooltip.void_disable"
                    : "gui.tooltip.void_enable";
                return createFormattedTooltip(headerKey, "gui.tooltip.void_description");
            }));
        }
    }

    private List<Component> createFormattedTooltip(String headerKey, String descriptionKey) {
        FontHelper.Palette HEADER_PALETTE = new FontHelper.Palette(styleFromColor(0x5391e1), styleFromColor(0x5391e1));
        FontHelper.Palette DESCRIPTION_PALETTE = new FontHelper.Palette(styleFromColor(0x6B9AD6), styleFromColor(0xBFD7F5));

        List<Component> tooltip = new ArrayList<>();

        Component header = CreateItemDrawerLang.translate(headerKey).component();
        List<Component> headerLines = FontHelper.cutTextComponent(
            header,
            HEADER_PALETTE
        );
        tooltip.addAll(headerLines);

        Component description = CreateItemDrawerLang.translate(descriptionKey).component();
        List<Component> descriptionLines = FontHelper.cutTextComponent(
            description,
            DESCRIPTION_PALETTE
        );
        tooltip.addAll(descriptionLines);

        return tooltip;
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

    private void sendTogglePacket(BlockPos pos, int slot, SlotTogglePacket.ToggleMode type, boolean value) {
        PacketDistributor.sendToServer(new SlotTogglePacket(pos, slot, type, value));
    }
}
