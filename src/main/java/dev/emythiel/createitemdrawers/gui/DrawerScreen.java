package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.gui.widgets.SmallIconButton;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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

public class DrawerScreen extends AbstractSimiContainerScreen<DrawerMenu> {

    // Inventory slot widget (x, y, size)
    private static final int INV_SLOT_WIDGET_X = 238;
    private static final int INV_SLOT_WIDGET_Y = 0;
    private static final int INV_SLOT_WIDGET_SIZE = 18;
    // Lock/Void widget size (width, height)
    private static final int LOCK_VOID_W = 9;
    private static final int LOCK_VOID_H = 9;

    protected IconButton renderItemsButton;
    protected IconButton renderCountsButton;
    protected IconButton renderIconsButton;

    protected List<AbstractWidget> settingsWidgets;

    private final Component optionEnabled = CreateItemDrawerLang.translate("gui.tooltip.option_enabled").component();
    private final Component optionDisabled = CreateItemDrawerLang.translate("gui.tooltip.option_disabled").component();

    private static final ResourceLocation TEXTURE =
        CreateItemDrawers.asResource("textures/gui/drawer.png");

    public DrawerScreen(DrawerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);

        this.imageWidth = 176;
        this.imageHeight = 213;

        settingsWidgets = new ArrayList<>();
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
        graphics.drawString(this.font, this.title, 87 - font.width(this.title) / 2, 4, 0x404040, false);

        // Player inventory title
        graphics.drawString(this.font, playerInventoryTitle, 8, 119, 0x404040, false);

        // Upgrade slot title
        {
            Component text = CreateItemDrawerLang.translate("gui.upgrade_slot").component();
            float scale = 0.70f;
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1f);
            int drawX = (int)(31.5 / scale) - font.width(text) / 2;
            int drawY = (int)(26 / scale);
            graphics.drawString(
                this.font,
                text,
                drawX, drawY,
                0x582424,
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
    protected void renderSlotContents(@NotNull GuiGraphics graphics, @NotNull ItemStack stack,
                                      Slot slot, String countString) {
        int hash = slot.x + slot.y * this.imageWidth;
        boolean isTemplateItem = false;

        // If drawer slot and empty but locked, show template item
        if (slot instanceof ReadOnlySlotItemHandler ro) {
            DrawerStorageBlockEntity be = menu.contentHolder;
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
        // If above 1mil, show count as 1m, 10m etc. Also, why? That's so many! But be better safe I guess
        if (count > 999999)
            s = (count / 1000000) + "m";

        // Position
        float scale = 0.60f;
        int textWidth = this.font.width(s);

        graphics.pose().pushPose();

        graphics.pose().translate(0, 0, 200); // Push text in front of item

        // Re-scale
        graphics.pose().scale(scale, scale, 1);

        // Convert slot coordinates to scaled space
        float inv = 1f / scale;

        // x,y in bottom right corner of slot
        int drawX = (int)((slot.x + 16) * inv) - textWidth;
        int drawY = (int)((slot.y + 11) * inv);

        graphics.drawString(this.font, s, drawX, drawY, 0xFFFFFF/*0xDDDDDD*/, true);

        graphics.pose().popPose();
    }

    @Override
    protected void init() {
        super.init();

        removeWidgets(settingsWidgets);
        settingsWidgets.clear();

        DrawerStorageBlockEntity be = menu.contentHolder;

        IconButton closeMenuBtn =
            new IconButton(leftPos + 148, topPos + 85, AllIcons.I_CONFIRM);
        closeMenuBtn.withCallback(this::onClose);
        addRenderableWidget(closeMenuBtn);

        renderItemsButton = new IconButton(leftPos + 8, topPos + 85, AllIcons.I_FX_SURFACE_ON);
        renderItemsButton.withCallback(() -> {
            boolean newVal = !be.getRenderItems();
            be.setRenderItems(newVal);
            sendTogglePacket(be.getBlockPos(), 0, ITEMS, newVal);
        });
        renderItemsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.items_header").component());
        addRenderableWidget(renderItemsButton);

        renderCountsButton = new IconButton(leftPos + 26, topPos + 85, AllIcons.I_HOUR_HAND_FIRST_24);
        renderCountsButton.withCallback(() -> {
            boolean newVal = !be.getRenderCounts();
            be.setRenderCounts(newVal);
            sendTogglePacket(be.getBlockPos(), 0, COUNTS, newVal);
        });
        renderCountsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.counts_header").component());
        addRenderableWidget(renderCountsButton);

        renderIconsButton = new IconButton(leftPos + 44, topPos + 85, AllIcons.I_PLACEMENT_SETTINGS);
        renderIconsButton.withCallback(() -> {
            boolean newVal = !be.getRenderIcons();
            be.setRenderIcons(newVal);
            sendTogglePacket(be.getBlockPos(), 0, ICONS, newVal);
        });
        renderIconsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.icons_header").component());
        addRenderableWidget(renderIconsButton);

        settingsWidgets.add(renderItemsButton);
        settingsWidgets.add(renderCountsButton);
        settingsWidgets.add(renderIconsButton);

        // Per slot void/lock toggles
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof ReadOnlySlotItemHandler roSlot)) continue;
            int slotCount = be.getStorage().getSlotCount();

            int slotIndex = roSlot.getSlotIndex();

            DrawerSlot drawerSlot = be.getStorage().getSlot(slotIndex);

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
            SmallIconButton lockButton = new SmallIconButton(toggleX, lockY, AllIcons.I_CONFIG_LOCKED)
                .withGreen(drawerSlot::isLockMode)
                .withTooltipKey("lock");
            lockButton.withCallback(() -> {
                boolean newVal = !drawerSlot.isLockMode();
                drawerSlot.setLockMode(newVal);
                sendTogglePacket(be.getBlockPos(), slotIndex, LOCK, newVal);
            });
            lockButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.lock_header").component());
            addRenderableWidget(lockButton);

            // Void mode
            SmallIconButton voidButton = new SmallIconButton(toggleX, voidY, AllIcons.I_CONFIG_DISCARD)
                .withGreen(drawerSlot::isVoidMode)
                .withTooltipKey("void");
            voidButton.withCallback(() -> {
                boolean newVal = !drawerSlot.isVoidMode();
                drawerSlot.setVoidMode(newVal);
                sendTogglePacket(be.getBlockPos(), slotIndex, VOID, newVal);
            });
            voidButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.void_header").component());
            addRenderableWidget(voidButton);

            settingsWidgets.add(lockButton);
            settingsWidgets.add(voidButton);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        DrawerStorageBlockEntity be = menu.contentHolder;

        renderItemsButton.green = be.getRenderItems();
        renderCountsButton.green = be.getRenderCounts();
        renderIconsButton.green = be.getRenderIcons();

        handleTooltips();
    }

    protected void handleTooltips() {
        for (AbstractWidget widget : settingsWidgets)
            if (widget instanceof IconButton button) {
                if (!button.getToolTip().isEmpty()) {
                    button.setToolTip(button.getToolTip().get(0));
                    button.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.BLUE, hasShiftDown()));
                }

                if (hasShiftDown()) {
                    if (button instanceof SmallIconButton smallBtn && smallBtn.getTooltipKey() != null) {
                        fillTooltip(button, smallBtn.getTooltipKey());
                    }
                }
            }

        if (hasShiftDown()) {
            fillTooltip(renderItemsButton, "items");
            fillTooltip(renderCountsButton, "counts");
            fillTooltip(renderIconsButton, "icons");
        }
    }

    private void fillTooltip(IconButton button, String tooltipKey) {
        if (!button.isHovered())
            return;

        boolean enabled = button.green;
        List<Component> tooltip = button.getToolTip();
        tooltip.add((enabled ? optionEnabled : optionDisabled).plainCopy()
            .withStyle(enabled ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        tooltip.addAll(TooltipHelper.cutTextComponent(
            CreateItemDrawerLang.translate("gui.tooltip." + tooltipKey + "_description").component(),
            FontHelper.Palette.ALL_GRAY
        ));
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
