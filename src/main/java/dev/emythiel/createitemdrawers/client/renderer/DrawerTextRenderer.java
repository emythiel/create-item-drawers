package dev.emythiel.createitemdrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import dev.emythiel.createitemdrawers.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DrawerTextRenderer {

    public static void renderSlotText(DrawerBlockEntity be, int slot, PoseStack ms,
                                      MultiBufferSource buffer, int light) {
        int count = be.getStorage().getSlot(slot).getCount();
        if (count <= 0) return;

        int slots = be.getStorage().getSlotCount();
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Vec3 uv = DrawerInteractionHelper.getTextUV(slot, slots);

        ms.pushPose();

        Font font = Minecraft.getInstance().font;
        String text = String.valueOf(count);
        Component textComponent = Component.literal(text);
        FormattedCharSequence formattedText = textComponent.getVisualOrderText();
        int textWidth = font.width(formattedText);

        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(Axis.YP.rotationDegrees(RenderHelper.getFaceRotation(facing)));

        ms.translate(uv.x - 0.5, uv.y - 0.5, 0.475);
        float scale = slots == 1 ? 0.02f : 0.01f;
        ms.scale(scale, -scale, scale);

        float xOffset = -textWidth / 2f;

        Minecraft.getInstance().font.drawInBatch(
            formattedText, xOffset, 0f, 0xFFFFFF, false,
            ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light
        );

        ms.popPose();
    }
}
