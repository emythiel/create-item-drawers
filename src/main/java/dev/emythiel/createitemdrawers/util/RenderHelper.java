package dev.emythiel.createitemdrawers.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    public static void addQuad(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                               float x1, float x2, float y1, float y2, float z,
                               float uMin, float uMax, float vMin, float vMax, Vector3f normal) {
        // x1=left, x2=right, y1=bottom, y2=top
        addVertex(matrix, vertexConsumer, light, overlay, x2, y1, z, uMax, vMin, normal);  // Bottom-right
        addVertex(matrix, vertexConsumer, light, overlay, x2, y2, z, uMax, vMax, normal);  // Top-right
        addVertex(matrix, vertexConsumer, light, overlay, x1, y2, z, uMin, vMax, normal);  // Top-left
        addVertex(matrix, vertexConsumer, light, overlay, x1, y1, z, uMin, vMin, normal);  // Bottom-left
    }

    public static void addVertex(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                                 float x, float y, float z, float u, float v, Vector3f normal) {
        vertexConsumer.addVertex(matrix, x, y, z)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal.x(), normal.y(), normal.z());
    }

    public static void renderFullTexture(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                                         Vector3f normal, float size) {
        float halfSize = size / 2f;
        addQuad(matrix, vertexConsumer, light, overlay, -halfSize, halfSize, -halfSize, halfSize, 0f,
            0f, 1f, 1f, 0f, normal);
    }

    public static void renderTextureRegion(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                                           float minX, float maxX, float minY, float maxY, float z,
                                           float uMin, float uMax, float vMin, float vMax, Vector3f normal) {
        addQuad(matrix, vertexConsumer, light, overlay, minX, maxX, minY, maxY, z, uMin, uMax, vMin, vMax, normal);
    }
}
