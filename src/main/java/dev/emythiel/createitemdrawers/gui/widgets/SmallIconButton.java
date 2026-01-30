package dev.emythiel.createitemdrawers.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/** Based on Create IconButton, but made smaller to fit the lock/void button requirements
 */
public class SmallIconButton extends IconButton {

    private BooleanSupplier greenSupplier;

    private String tooltipKey;

    public SmallIconButton(int x, int y, ScreenElement icon) {
        this(x, y, 9, 9, icon);
    }

    public SmallIconButton(int x, int y, int w, int h, ScreenElement icon) {
        super(x, y, w, h, icon);
        this.icon = icon;
    }

    public SmallIconButton withGreen(BooleanSupplier supplier) {
        this.greenSupplier = supplier;
        return this;
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;

            green = greenSupplier != null && greenSupplier.getAsBoolean();

            AllGuiTextures button = !active ? AllGuiTextures.BUTTON_DISABLED
                : isHovered && AllKeys.isMouseButtonDown(0) ? AllGuiTextures.BUTTON_DOWN
                : isHovered ? AllGuiTextures.BUTTON_HOVER
                : green ? AllGuiTextures.BUTTON_GREEN : AllGuiTextures.BUTTON;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawBg(graphics, button);

            // Rescale icon
            graphics.pose().pushPose();

            float scale = 0.5f;
            graphics.pose().scale(scale, scale, 1);

            float centerX = getX() + width / 2f;
            float centerY = getY() + height / 2f;

            int iconX = (int) ((centerX - 8 * scale) / scale);
            int iconY = (int) ((centerY - 8 * scale) / scale);

            icon.render(graphics, iconX, iconY);

            graphics.pose().popPose();
        }
    }

    @Override
    protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
        graphics.blit(
            button.location,
            getX(), getY(),
            width, height,
            button.getStartX(), button.getStartY(),
            button.getWidth(), button.getHeight(),
            256, 256
        );
    }

    public SmallIconButton withTooltipKey(String key) {
        this.tooltipKey = key;
        return this;
    }

    public String getTooltipKey() {
        return tooltipKey;
    }
}
