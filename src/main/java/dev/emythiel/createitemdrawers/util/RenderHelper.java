package dev.emythiel.createitemdrawers.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.ModItems;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;

public class RenderHelper {
    public static float getFaceRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 180;
            case SOUTH -> 0;
            case WEST -> -90;
            case EAST -> 90;
            default -> 0;
        };
    }

    public static Vec3 worldToLocalFace(Vec3 world, BlockPos pos, Direction facing) {
        Vec3 local = world.subtract(Vec3.atLowerCornerOf(pos));
        return VecHelper.rotateCentered(local, facing.getOpposite().toYRot(), Direction.Axis.Y);
    }

    public static Vec3 faceUVToWorld(BlockPos pos, Direction face, double u, double v, double depth) {
        Vec3 base = Vec3.atLowerCornerOf(pos);

        // Flip u, so x axis isn't wrong
        u = 1 - u;

        return switch (face) {
            case NORTH -> base.add(u, v, depth);
            case SOUTH -> base.add(1 - u, v, 1 - depth);
            case WEST  -> base.add(depth, v, 1 - u);
            case EAST  -> base.add(1 - depth, v, u);
            default    -> base;
        };
    }

    public static void renderSlotItem(ItemRenderer itemRenderer, ItemStack stack, int slot, int slots,
                                       PoseStack ms, MultiBufferSource buffer, int light) {
        Level level = Minecraft.getInstance().level;

        Vec3 uv = DrawerInteractionHelper.getSlotUV(slot, slots);

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.5F : 0.25f;
        ms.scale(scale, scale, 0.001f);

        itemRenderer.renderStatic(stack, ItemDisplayContext.GUI, light, OverlayTexture.NO_OVERLAY,
            ms, buffer, level, 0);

        ms.popPose();
    }

    public static void renderSlotCount(String count, int slot, int slots, PoseStack ms, MultiBufferSource buffer, int light) {
        Font font = Minecraft.getInstance().font;

        Vec3 uv = DrawerInteractionHelper.getCountUV(slot, slots);

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.02f : 0.01f;
        ms.scale(scale, -scale, scale);
        float xOffset = -font.width(count) / 2f;

        font.drawInBatch(count, xOffset, 0, 0xFFFFFF, false,
            ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        ms.popPose();
    }

    public static void renderSlotMode(RenderHelper.DrawerIcon mode, int slot, int slots,
                                      PoseStack ms, MultiBufferSource buffer, int light) {
        Vec3 uv;

        if (mode == DrawerIcon.LOCK)
            uv = DrawerInteractionHelper.getLockUV(slot, slots);
        else if (mode == DrawerIcon.VOID)
            uv = DrawerInteractionHelper.getVoidUV(slot, slots);
        else
            return;

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.15f : 0.08f;
        ms.scale(scale, scale, scale);

        Matrix4f matrix = ms.last().pose();
        Vector3f normal = new Vector3f(0, 0, 1);

        renderIconFromAtlas(matrix, buffer, light, OverlayTexture.NO_OVERLAY, normal,
            0, 0, 0, 1f, mode);

        ms.popPose();
    }

    public static void renderDrawerUpgrade(ItemStack upgrade, int slots,
                                            PoseStack ms, MultiBufferSource buffer, int light) {
        Map<Item, DrawerIcon> UPGRADE_ICONS = Map.of(
            ModItems.CAPACITY_UPGRADE_T1.get(), DrawerIcon.TIER_1,
            ModItems.CAPACITY_UPGRADE_T2.get(), DrawerIcon.TIER_2,
            ModItems.CAPACITY_UPGRADE_T3.get(), DrawerIcon.TIER_3,
            ModItems.CAPACITY_UPGRADE_T4.get(), DrawerIcon.TIER_4,
            ModItems.CAPACITY_UPGRADE_T5.get(), DrawerIcon.TIER_5
        );

        DrawerIcon icon = UPGRADE_ICONS.get(upgrade.getItem());
        if (icon == null)
            return;

        Vec3 uv = DrawerInteractionHelper.getUpgradeUV(slots);

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.12f : 0.08f;
        ms.scale(scale, scale, scale);

        Matrix4f matrix = ms.last().pose();
        Vector3f normal = new Vector3f(0, 0, 1);

        renderIconFromAtlas(matrix, buffer, light, OverlayTexture.NO_OVERLAY, normal,
            0, 0, 0, 1f, icon);

        ms.popPose();
    }

    public enum DrawerIcon {
        TIER_1(0, 0),
        TIER_2(16,0),
        TIER_3(32, 0),
        TIER_4(48, 0),
        TIER_5(64, 0),
        LOCK(0, 16),
        VOID(16, 16);

        private static final float ATLAS_SIZE = 256f;
        private static final float ICON_SIZE = 16f;

        private final float uMin;
        private final float uMax;
        private final float vMin;
        private final float vMax;

        DrawerIcon (int x, int y) {
            this.uMin = x / ATLAS_SIZE;
            this.uMax = (x + ICON_SIZE) / ATLAS_SIZE;
            this.vMin = y / ATLAS_SIZE;
            this.vMax = (y + ICON_SIZE) / ATLAS_SIZE;
        }

        public float getUMin() { return uMin; }
        public float getUMax() { return uMax; }
        public float getVMin() { return vMin; }
        public float getVMax() { return vMax; }
    }

    private static final ResourceLocation ATLAS = CreateItemDrawers.asResource("textures/sprite/icons.png");
    public static void renderIconFromAtlas(Matrix4f matrix, MultiBufferSource buffer, int light, int overlay,
                                           Vector3f normal, float x, float y, float z, float size,
                                           DrawerIcon icon) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(ATLAS));
        float halfSize = size / 2f;

        renderTextureRegion(matrix, vertexConsumer, light, overlay,
            x - halfSize, x + halfSize, y - halfSize, y + halfSize, z,
            icon.getUMin(), icon.getUMax(), icon.getVMin(), icon.getVMax(), normal);
    }

    private static void renderTextureRegion(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                                           float minX, float maxX, float minY, float maxY, float z,
                                           float uMin, float uMax, float vMin, float vMax, Vector3f normal) {
        addQuad(matrix, vertexConsumer, light, overlay, minX, maxX, minY, maxY, z, uMin, uMax, vMin, vMax, normal);
    }

    private static void addQuad(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                               float x1, float x2, float y1, float y2, float z,
                               float uMin, float uMax, float vMin, float vMax, Vector3f normal) {
        addVertex(matrix, vertexConsumer, light, overlay, x2, y1, z, uMax, vMax, normal);
        addVertex(matrix, vertexConsumer, light, overlay, x2, y2, z, uMax, vMin, normal);
        addVertex(matrix, vertexConsumer, light, overlay, x1, y2, z, uMin, vMin, normal);
        addVertex(matrix, vertexConsumer, light, overlay, x1, y1, z, uMin, vMax, normal);
    }

    private static void addVertex(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                                 float x, float y, float z, float u, float v, Vector3f normal) {
        vertexConsumer.addVertex(matrix, x, y, z)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal.x(), normal.y(), normal.z());
    }
}
