package dev.emythiel.createitemdrawers.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    private static final ResourceLocation ATLAS = CreateItemDrawers.asResource("textures/sprite/icons.png");

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
