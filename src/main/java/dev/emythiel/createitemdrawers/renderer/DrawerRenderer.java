package dev.emythiel.createitemdrawers.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DrawerRenderer implements BlockEntityRenderer<DrawerBlockEntity> {
    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DrawerBlockEntity be, float partialTicks,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        // TODO:
        // Item rendering
        // Text rendering
        // Square highlights
        // Wrench overlays
    }
}
