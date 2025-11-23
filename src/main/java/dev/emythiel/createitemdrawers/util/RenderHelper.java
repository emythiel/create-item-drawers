package dev.emythiel.createitemdrawers.util;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

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

        return switch (face) {
            case NORTH -> base.add(u, v, depth);
            case SOUTH -> base.add(1 - u, v, 1 - depth);
            case WEST  -> base.add(depth, v, 1 - u);
            case EAST  -> base.add(1 - depth, v, u);
            default    -> base;
        };
    }
}
