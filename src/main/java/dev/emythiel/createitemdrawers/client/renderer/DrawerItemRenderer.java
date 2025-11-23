package dev.emythiel.createitemdrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import dev.emythiel.createitemdrawers.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DrawerItemRenderer {

    public static void renderSlotItem(DrawerBlockEntity be, int slot, PoseStack ms,
                                      MultiBufferSource buffer, int light, int overlay) {

        ItemStack stack = be.getStorage().getSlot(slot).getStoredItem();
        if (stack.isEmpty()) return;

        int slots = be.getStorage().getSlotCount();
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Vec3 uv = DrawerInteractionHelper.getSlotUV(slot, slots);

        ms.pushPose();

        ms.translate(0.5, 0.5, 0.5); // Center block first

        ms.mulPose(Axis.YP.rotationDegrees(RenderHelper.getFaceRotation(facing))); // Rotate towards proper face

        ms.translate(uv.x - 0.5, uv.y - 0.5, 0.47); // Apply local UV offset

        // Scale item
        float scale = slots == 1 ? 0.5f : 0.25f;
        ms.scale(scale, scale, 0.001f);

       Minecraft.getInstance().getItemRenderer().renderStatic(
           stack, ItemDisplayContext.GUI, light, overlay, ms, buffer, null, 0
       );

        ms.popPose();
    }
}
