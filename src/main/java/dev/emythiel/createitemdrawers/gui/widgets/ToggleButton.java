package dev.emythiel.createitemdrawers.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ToggleButton extends AbstractWidget {

    private final ResourceLocation texture;
    private final int onU, onV;
    private final int offU, offV;
    private final int texW, texH;

    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public ToggleButton(
        int x, int y,
        ResourceLocation texture,
        int offU, int offV,
        int onU, int onV,
        int w, int h,
        BooleanSupplier getter,
        Consumer<Boolean> setter
    ) {
        super(x, y, w, h, Component.empty());

        this.texture = texture;
        this.offU = offU;
        this.offV = offV;
        this.onU = onU;
        this.onV = onV;
        this.texW = w;
        this.texH = h;

        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        boolean isOn = getter.getAsBoolean();

        int u = isOn ? onU : offU;
        int v = isOn ? onV : offV;

        graphics.blit(texture, getX(), getY(), u, v, texW, texH);

        /*if (isHovered) {
            graphics.fill(getX(), getY(), getX() + texW, getY() + texH, 0x40FFFFFF);
        }*/
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            setter.accept(!getter.getAsBoolean());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
