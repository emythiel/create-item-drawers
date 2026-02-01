package dev.emythiel.createitemdrawers.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

    public static AABB getSlotAABB(DrawerStorageBlockEntity be, int slot) {
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

    public static void renderSlotItem(ItemRenderer itemRenderer, ItemStack stack, int slot, int slots,
                                       PoseStack ms, MultiBufferSource buffer, int light) {
        Level level = Minecraft.getInstance().level;

        Vec3 uv = getSlotUV(slot, slots);

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

        Vec3 uv = getCountUV(slot, slots);

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.02f : 0.01f;
        ms.scale(scale, -scale, scale);
        float xOffset = -font.width(count) / 2f;

        font.drawInBatch(count, xOffset, 0, 0xFFFFFF, false,
            ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        ms.popPose();
    }

    public static void renderSlotMode(DrawerIcons.Icon icon, int slot, int slots,
                                      PoseStack ms, MultiBufferSource buffer, int light) {
        if (icon == null)
            return;

        Vec3 uv;

        if (icon == DrawerIcons.LOCK)
            uv = getLockUV(slot, slots);
        else if (icon == DrawerIcons.VOID)
            uv = getVoidUV(slot, slots);
        else
            return;

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.15f : 0.08f;
        ms.scale(scale, scale, scale);

        icon.renderWorld(ms, buffer, light, OverlayTexture.NO_OVERLAY, 1f);

        ms.popPose();
    }

    public static void renderDrawerUpgrade(ItemStack upgrade, int slots,
                                            PoseStack ms, MultiBufferSource buffer, int light) {
        Map<Item, DrawerIcons.Icon> UPGRADE_ICONS = Map.of(
            ModItems.CAPACITY_UPGRADE_T1.get(), DrawerIcons.UPGRADE_TIER_1,
            ModItems.CAPACITY_UPGRADE_T2.get(), DrawerIcons.UPGRADE_TIER_2,
            ModItems.CAPACITY_UPGRADE_T3.get(), DrawerIcons.UPGRADE_TIER_3,
            ModItems.CAPACITY_UPGRADE_T4.get(), DrawerIcons.UPGRADE_TIER_4,
            ModItems.CAPACITY_UPGRADE_T5.get(), DrawerIcons.UPGRADE_TIER_5
        );

        DrawerIcons.Icon icon = UPGRADE_ICONS.get(upgrade.getItem());
        if (icon == null)
            return;

        Vec3 uv = getUpgradeUV(slots);

        ms.pushPose();
        ms.translate(uv.x, uv.y, uv.z);
        float scale = slots == 1 ? 0.12f : 0.08f;
        ms.scale(scale, scale, scale);

        icon.renderWorld(ms, buffer, light, OverlayTexture.NO_OVERLAY, 1f);

        ms.popPose();
    }

    private static Vec3 getSlotUV(int slot, int slotCount) {
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

    private static Vec3 getCountUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.30 : 0.135;
        return new Vec3(uv.x, uv.y - shift, 0.001);
    }

    private static Vec3 getLockUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.35 : 0.165;
        return new Vec3(uv.x - (shift / 1.5), uv.y + shift, 0.001);
    }

    private static Vec3 getVoidUV(int slot, int slotCount) {
        Vec3 uv = getSlotUV(slot, slotCount);
        double shift = (slotCount == 1) ? 0.35 : 0.165;
        return new Vec3(uv.x + (shift / 1.5), uv.y + shift, 0.001);
    }

    private static Vec3 getUpgradeUV(int slotCount) {
        double shift = (slotCount == 1) ? 0.453 : 0.468;
        return new Vec3(0, shift, 0.031);
    }
}
