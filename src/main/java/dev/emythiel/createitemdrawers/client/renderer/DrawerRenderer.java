package dev.emythiel.createitemdrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class DrawerRenderer implements BlockEntityRenderer<DrawerBlockEntity> {
    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DrawerBlockEntity be, float partialTicks,
                       PoseStack ms, MultiBufferSource buffer, int packedLight, int overlay) {

        Level level = be.getLevel();
        BlockPos facePos = be.getBlockPos().relative(be.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
        int light = level != null ? LevelRenderer.getLightColor(level, facePos) : LightTexture.pack(15, 15);
        for (int slot = 0; slot < be.getStorage().getSlotCount(); slot++) {
            DrawerItemRenderer.renderSlotItem(be, slot, ms, buffer, light, overlay);
            DrawerTextRenderer.renderSlotText(be, slot, ms, buffer, light);
        }
    }
}
