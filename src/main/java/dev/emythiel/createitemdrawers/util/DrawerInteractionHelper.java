package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DrawerInteractionHelper {

    public static int getHitSlot(DrawerStorageBlockEntity be, Vec3 hitPos) {
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        BlockPos pos = be.getBlockPos();

        Vec3 local = worldToLocalFace(hitPos, pos, facing);
        double x = local.x();
        double y = local.y();

        int slots = be.getStorage().getSlotCount();

        return switch (slots) {
            case 1 -> 0;
            case 2 -> (y > 0.5) ? 0 : 1;
            case 4 -> {
                int row = (y > 0.5) ? 0 : 1;
                int col = (x > 0.5) ? 0 : 1;
                yield row * 2 + col;
            }
            default -> -1;
        };
    }

    private static Vec3 worldToLocalFace(Vec3 world, BlockPos pos, Direction facing) {
        Vec3 local = world.subtract(Vec3.atLowerCornerOf(pos));
        return VecHelper.rotateCentered(local, facing.getOpposite().toYRot(), Direction.Axis.Y);
    }
}
