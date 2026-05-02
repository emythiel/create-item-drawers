package dev.emythiel.createitemdrawers.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.gui.widgets.SmallIconButton;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import dev.emythiel.createitemdrawers.renderer.DrawerIcons;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.emythiel.createitemdrawers.network.SlotTogglePacket.ToggleMode.*;

public abstract class AbstractDrawerScreen<M extends AbstractDrawerMenu<?>> extends AbstractSimiContainerScreen<M> {

    protected static final ResourceLocation TEXTURE = CreateItemDrawers.asResource("textures/gui/drawer.png");

    protected static final int INV_SLOT_WIDGET_X = 238;
    protected static final int INV_SLOT_WIDGET_Y = 0;
    protected static final int INV_SLOT_WIDGET_SIZE = 18;
    protected static final int LOCK_VOID_W = 9;
    protected static final int LOCK_VOID_H = 9;

    protected IconButton renderItemsButton;
    protected IconButton renderCountsButton;
    protected IconButton renderIconsButton;
    protected List<AbstractWidget> settingsWidgets;

    private final Component optionEnabled =
        CreateItemDrawerLang.translate("gui.tooltip.option_enabled").component();
    private final Component optionDisabled =
        CreateItemDrawerLang.translate("gui.tooltip.option_disabled").component();

    protected AbstractDrawerScreen(M menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 213;
        this.settingsWidgets = new ArrayList<>();
    }

    protected abstract void sendTogglePacket(int slot, SlotTogglePacket.ToggleMode mode, boolean value);

    protected IDrawerGuiHolder holder() {
        return menu.getHolder();
    }

    // Rendering
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
        graphics.drawString(this.font, this.title, 87 - font.width(this.title) / 2, 4, 0x404040, false);
        graphics.drawString(this.font, playerInventoryTitle, 8, 119, 0x404040, false);

        Component text = CreateItemDrawerLang.translate("gui.upgrade_slot").component();
        float scale = 0.70f;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1f);
        int drawX = (int)(31.5 / scale) - font.width(text) / 2;
        int drawY = (int)(26 / scale);
        graphics.drawString(this.font, text, drawX, drawY, 0x582424, false);
        graphics.pose().popPose();
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

        if (slot instanceof DrawerSlotItemHandler ro) {
            DrawerSlot drawerSlot = holder().getDrawerSlot(ro.getSlotIndex());
            if (drawerSlot != null
                && drawerSlot.isLockMode()
                && !drawerSlot.getStoredItem().isEmpty()
                && drawerSlot.getCount() == 0) {
                stack = drawerSlot.getStoredItem();
                isTemplateItem = true;
            }
        }

        graphics.renderItem(stack, slot.x, slot.y, hash);

        if (isTemplateItem) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 275);
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80AAAAAA);
            graphics.pose().popPose();
            return;
        }

        if (!(slot instanceof DrawerSlotItemHandler)) {
            graphics.renderItemDecorations(this.font, stack, slot.x, slot.y, countString);
            return;
        }

        if (stack.isEmpty()) return;

        int count = stack.getCount();
        String s = (count > 9999) ? (count / 1000) + "k" : String.valueOf(count);
        if (count > 999999) s = (count / 1000000) + "m";

        float scale = 0.60f;
        int textWidth = this.font.width(s);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        graphics.pose().scale(scale, scale, 1);

        float inv = 1f / scale;
        int drawX = (int)((slot.x + 16) * inv) - textWidth;
        int drawY = (int)((slot.y + 11) * inv);
        graphics.drawString(this.font, s, drawX, drawY, 0xFFFFFF, true);

        graphics.pose().popPose();
    }

    private void drawSlotBackgrounds(GuiGraphics graphics) {
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof DrawerSlotItemHandler)) continue;
            int x = leftPos + slot.x - 1;
            int y = topPos + slot.y - 1;
            graphics.blit(TEXTURE, x, y, INV_SLOT_WIDGET_X, INV_SLOT_WIDGET_Y,
                INV_SLOT_WIDGET_SIZE, INV_SLOT_WIDGET_SIZE);
        }
    }

    // Init / Buttons
    @Override
    protected void init() {
        super.init();

        removeWidgets(settingsWidgets);
        settingsWidgets.clear();

        IDrawerGuiHolder holder = holder();

        IconButton closeMenuBtn = new IconButton(leftPos + 148, topPos + 85, AllIcons.I_CONFIRM);
        closeMenuBtn.withCallback(this::onClose);
        addRenderableWidget(closeMenuBtn);

        renderItemsButton = new IconButton(leftPos + 8, topPos + 85, DrawerIcons.ITEMS);
        renderItemsButton.withCallback(() -> {
            boolean newVal = !holder.getRenderItems();
            holder.setRenderItems(newVal);
            sendTogglePacket(0, ITEMS, newVal);
        });
        renderItemsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.items_header").component());
        addRenderableWidget(renderItemsButton);

        renderCountsButton = new IconButton(leftPos + 26, topPos + 85, DrawerIcons.COUNTS);
        renderCountsButton.withCallback(() -> {
            boolean newVal = !holder.getRenderCounts();
            holder.setRenderCounts(newVal);
            sendTogglePacket(0, COUNTS, newVal);
        });
        renderCountsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.counts_header").component());
        addRenderableWidget(renderCountsButton);

        renderIconsButton = new IconButton(leftPos + 44, topPos + 85, DrawerIcons.MODES);
        renderIconsButton.withCallback(() -> {
            boolean newVal = !holder.getRenderIcons();
            holder.setRenderIcons(newVal);
            sendTogglePacket(0, ICONS, newVal);
        });
        renderIconsButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.icons_header").component());
        addRenderableWidget(renderIconsButton);

        settingsWidgets.add(renderItemsButton);
        settingsWidgets.add(renderCountsButton);
        settingsWidgets.add(renderIconsButton);

        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof DrawerSlotItemHandler roSlot)) continue;

            int slotCount = holder.getSlotCount();
            int slotIndex = roSlot.getSlotIndex();
            DrawerSlot drawerSlot = holder.getDrawerSlot(slotIndex);
            if (drawerSlot == null) continue;

            int sx = leftPos + slot.x;
            int sy = topPos + slot.y;
            int toggleX = sx - LOCK_VOID_W - 1;
            if (slotCount == 4 && slotIndex % 2 == 1)
                toggleX = sx + INV_SLOT_WIDGET_SIZE - 1;

            int lockY = sy - 1;
            int voidY = lockY + LOCK_VOID_H;

            SmallIconButton lockButton = new SmallIconButton(toggleX, lockY, DrawerIcons.LOCK)
                .withGreen(drawerSlot::isLockMode)
                .withTooltipKey("lock");
            lockButton.withCallback(() -> {
                boolean newVal = !drawerSlot.isLockMode();
                drawerSlot.setLockMode(newVal);
                sendTogglePacket(slotIndex, LOCK, newVal);
            });
            lockButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.lock_header").component());
            addRenderableWidget(lockButton);

            SmallIconButton voidButton = new SmallIconButton(toggleX, voidY, DrawerIcons.VOID)
                .withGreen(drawerSlot::isVoidMode)
                .withTooltipKey("void");
            voidButton.withCallback(() -> {
                boolean newVal = !drawerSlot.isVoidMode();
                drawerSlot.setVoidMode(newVal);
                sendTogglePacket(slotIndex, VOID, newVal);
            });
            voidButton.setToolTip(CreateItemDrawerLang.translate("gui.tooltip.void_header").component());
            addRenderableWidget(voidButton);

            settingsWidgets.add(lockButton);
            settingsWidgets.add(voidButton);
        }
    }

    // Tick / Tooltips
    @Override
    protected void containerTick() {
        super.containerTick();

        IDrawerGuiHolder holder = holder();
        renderItemsButton.green  = holder.getRenderItems();
        renderCountsButton.green = holder.getRenderCounts();
        renderIconsButton.green  = holder.getRenderIcons();

        handleTooltips();
    }

    protected void handleTooltips() {
        for (AbstractWidget widget : settingsWidgets) {
            if (!(widget instanceof IconButton button)) continue;

            if (!button.getToolTip().isEmpty()) {
                button.setToolTip(button.getToolTip().get(0));
                button.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.BLUE, hasShiftDown()));
            }

            if (hasShiftDown() && button instanceof SmallIconButton smallBtn
                && smallBtn.getTooltipKey() != null) {
                fillTooltip(button, smallBtn.getTooltipKey());
            }
        }

        if (hasShiftDown()) {
            fillTooltip(renderItemsButton, "items");
            fillTooltip(renderCountsButton, "counts");
            fillTooltip(renderIconsButton, "icons");
        }
    }

    private void fillTooltip(IconButton button, String tooltipKey) {
        if (!button.isHovered()) return;

        boolean enabled = button.green;
        List<Component> tooltip = button.getToolTip();
        tooltip.add((enabled ? optionEnabled : optionDisabled).plainCopy()
            .withStyle(enabled ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        tooltip.addAll(TooltipHelper.cutTextComponent(
            CreateItemDrawerLang.translate("gui.tooltip." + tooltipKey + "_description").component(),
            FontHelper.Palette.ALL_GRAY
        ));
    }
}
