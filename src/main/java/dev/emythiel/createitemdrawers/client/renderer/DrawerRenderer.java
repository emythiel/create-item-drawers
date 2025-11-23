package dev.emythiel.createitemdrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DrawerRenderer implements BlockEntityRenderer<DrawerBlockEntity> {
    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DrawerBlockEntity be, float partialTicks,
                       PoseStack ms, MultiBufferSource buffer, int packedLight, int overlay) {

        Level level = be.getLevel();
        BlockPos facePos = be.getBlockPos().relative(be.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
        int light = level != null ? LevelRenderer.getLightColor(level, facePos) : LightTexture.pack(15, 15);

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        // Check player distance
        double distSq = player.distanceToSqr(
            be.getBlockPos().getX() + 0.5,
            be.getBlockPos().getY() + 0.5,
            be.getBlockPos().getZ() + 0.5
        );

        int itemDist = ClientConfig.ITEM_RENDER_DISTANCE.get();
        int textDist = ClientConfig.TEXT_RENDER_DISTANCE.get();

        boolean items = distSq <= itemDist * itemDist;
        boolean texts = distSq <= textDist * textDist;

        if (!items && texts)
            return;

        // Check if player is in front of block (don't render if behind)
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Vec3 frontNormal = new Vec3(
            facing.getStepX(),
            facing.getStepY(),
            facing.getStepZ()
        ).normalize();

        Vec3 toPlayer = new Vec3(
            player.getX() - (be.getBlockPos().getX() + 0.5),
            player.getY() - (be.getBlockPos().getY() + 0.5),
            player.getZ() - (be.getBlockPos().getZ() + 0.5)
        ).normalize();

        if (frontNormal.dot(toPlayer) <= 0)
            return;

        for (int slot = 0; slot < be.getStorage().getSlotCount(); slot++) {
            if (items)
                DrawerItemRenderer.renderSlotItem(be, slot, ms, buffer, light, overlay);
            if (texts)
                DrawerTextRenderer.renderSlotText(be, slot, ms, buffer, light);
        }
    }
}
