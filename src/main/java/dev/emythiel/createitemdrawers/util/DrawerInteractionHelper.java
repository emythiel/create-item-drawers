package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DrawerInteractionHelper {
    public static Vec3 getSlotUV(int slot, int slotCount) {
        return switch (slotCount) {
            case 1 -> new Vec3(0, 0, 0);
            case 2 -> new Vec3(0, slot == 0 ? 0.25 : -0.25, 0);
            case 4 -> {
                double x = (slot % 2 == 0) ? -0.25 : 0.25;
                double y = (slot < 2) ? 0.25 : -0.25;
                yield new Vec3(x, y, 0);
            }
            default -> new Vec3(0.5, 0.5, 0);
        };
    }

    public static Vec3 getTextUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.30 : 0.135;
        return new Vec3(uv.x, uv.y - shift, 0.001);
    }

    public static Vec3 getLockUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.35 : 0.165;
        return new Vec3(uv.x - (shift / 1.5), uv.y + shift, 0.001);
    }

    public static Vec3 getVoidUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.35 : 0.165;
        return new Vec3(uv.x + (shift / 1.5), uv.y + shift, 0.001);
    }

    public static Vec3 getUpgradeUV(int slotCount) {
        double shift = (slotCount == 1) ? 0.453 : 0.468;
        return new Vec3(0, shift, 0.031);
    }

    public static int getHitSlot(DrawerBlockEntity be, Vec3 hitPos) {
        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        BlockPos pos = be.getBlockPos();

        Vec3 local = RenderHelper.worldToLocalFace(hitPos, pos, facing);
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

    public static AABB getSlotAABB(DrawerBlockEntity be, int slot) {
        BlockPos pos = be.getBlockPos();
        int slots = be.getStorage().getSlotCount();

        Vec3 uv = getSlotUV(slot, slots);

        float scale = (slots == 1) ? 0.5f : 0.25f;
        float half = scale * 0.5f;

        double minX = uv.x - half + 0.5f;
        double maxX = uv.x + half + 0.5f;

        double minY = uv.y - half + 0.5f;
        double maxY = uv.y + half + 0.5f;

        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Vec3 p1 = RenderHelper.faceUVToWorld(pos, facing, minX, minY, 0.035);
        Vec3 p2 = RenderHelper.faceUVToWorld(pos, facing, maxX, maxY, 0.035);

        return new AABB(p1, p2);
    }
}
