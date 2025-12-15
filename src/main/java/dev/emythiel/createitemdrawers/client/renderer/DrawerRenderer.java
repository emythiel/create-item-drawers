package dev.emythiel.createitemdrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.config.ClientConfig;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import dev.emythiel.createitemdrawers.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DrawerRenderer extends SafeBlockEntityRenderer<DrawerBlockEntity> {
    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    private static int itemDist = ClientConfig.ITEM_RENDER_DISTANCE.get();
    private static int textDist = ClientConfig.TEXT_RENDER_DISTANCE.get();

    @Override
    protected void renderSafe(DrawerBlockEntity be, float partialTicks,
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

        boolean items = be.getRenderItems() && distSq <= itemDist * itemDist;
        boolean texts = be.getRenderCounts() && distSq <= textDist * textDist;

        if (!items && !texts)
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
                renderSlotItem(be, slot, ms, buffer, light, overlay);
            if (texts)
                renderSlotText(be, slot, ms, buffer, light);
        }
    }

    private static void renderSlotItem(DrawerBlockEntity be, int slot, PoseStack ms,
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

    private static void renderSlotText(DrawerBlockEntity be, int slot, PoseStack ms,
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
