package dev.emythiel.createitemdrawers.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModConfigs;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static dev.emythiel.createitemdrawers.block.base.BaseDrawerBlock.HORIZONTAL_FACING;

public class DrawerRenderer extends SafeBlockEntityRenderer<DrawerStorageBlockEntity> {

    private Vec3 lastCameraPos = Vec3.ZERO;

    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public boolean shouldRender(DrawerStorageBlockEntity be, Vec3 cameraPos) {
        // Ponder scenes should always render, so skip all culling
        if (be.getLevel() instanceof PonderLevel) return true;

        // General check - if all 3 are disabled, just skip all culling stuff
        boolean shouldRenderItems = ModConfigs.client().renderItems.get();
        boolean shouldRenderCounts = ModConfigs.client().renderCounts.get();
        boolean shouldRenderIcons = ModConfigs.client().renderIcons.get();
        if (!shouldRenderItems && !shouldRenderCounts && !shouldRenderIcons) return false;

        // Distance check
        int itemDist = ModConfigs.client().renderItemsDistance.get();
        int countDist = ModConfigs.client().renderCountsDistance.get();
        int iconDist = ModConfigs.client().renderIconsDistance.get();

        double dx = cameraPos.x - (be.getBlockPos().getX() + 0.5);
        double dy = cameraPos.y - (be.getBlockPos().getY() + 0.5);
        double dz = cameraPos.z - (be.getBlockPos().getZ() + 0.5);
        double distSq = dx * dx + dy * dy + dz * dz;

        boolean anyVisible =
            (be.getRenderItems() && shouldRenderItems && distSq <= (double) itemDist * itemDist) ||
            (be.getRenderCounts() && shouldRenderCounts && distSq <= (double) countDist * countDist) ||
            (be.getRenderIcons() && shouldRenderIcons && distSq <= (double) iconDist * iconDist);
        if (!anyVisible) return false;

        // Facing check
        Direction facing = be.getBlockState().getValue(HORIZONTAL_FACING);
        Vec3 frontNormal = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Vec3 toCamera = new Vec3(dx, dy, dz).normalize();
        if (frontNormal.dot(toCamera) <= 0) return false;

        this.lastCameraPos = cameraPos;
        return true;
    }

    @Override
    protected void renderSafe(DrawerStorageBlockEntity be, float partialTicks,
                              PoseStack ms, MultiBufferSource buffer, int packedLight, int overlay) {

        boolean shouldRenderItems = ModConfigs.client().renderItems.get();
        boolean shouldRenderCounts = ModConfigs.client().renderCounts.get();
        boolean shouldRenderIcons = ModConfigs.client().renderIcons.get();

        boolean isPonderScene = be.getLevel() instanceof PonderLevel;

        int itemDist = ModConfigs.client().renderItemsDistance.get();
        int countDist = ModConfigs.client().renderCountsDistance.get();
        int iconDist = ModConfigs.client().renderIconsDistance.get();

        Vec3 cam = isPonderScene
            ? Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()
            : this.lastCameraPos;

        double dx = cam.x - (be.getBlockPos().getX() + 0.5);
        double dy = cam.y - (be.getBlockPos().getY() + 0.5);
        double dz = cam.z - (be.getBlockPos().getZ() + 0.5);
        double distSq = dx * dx + dy * dy + dz * dz;

        boolean renderItems = isPonderScene || (be.getRenderItems()  && shouldRenderItems  && distSq <= (double) itemDist  * itemDist);
        boolean renderCounts = isPonderScene || (be.getRenderCounts() && shouldRenderCounts && distSq <= (double) countDist * countDist);
        boolean renderIcons = isPonderScene || (be.getRenderIcons()  && shouldRenderIcons  && distSq <= (double) iconDist  * iconDist);

        Minecraft mc = Minecraft.getInstance();
        Direction facing = be.getBlockState().getValue(HORIZONTAL_FACING);
        Level level = be.getLevel();
        BlockPos facePos = be.getBlockPos().relative(facing);
        int light = level != null ? LevelRenderer.getLightColor(level, facePos) : LightTexture.pack(15, 15);

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(Axis.YP.rotationDegrees(RenderHelper.getFaceRotation(facing)));
        ms.translate(0, 0, 0.47);

        int slotCount = be.getStorage().getSlotCount();

        if (renderIcons && !be.getUpgrade().isEmpty())
            RenderHelper.renderDrawerUpgrade(be.getUpgrade(), slotCount, ms, buffer, light);

        for (int slot = 0; slot < slotCount; slot++) {
            DrawerSlot currentSlot = be.getStorage().getSlot(slot);
            ItemStack storedItem = currentSlot.getStoredItem();
            int count = currentSlot.getCount();
            boolean lockMode = currentSlot.isLockMode();
            boolean voidMode = currentSlot.isVoidMode();
            if (!storedItem.isEmpty() && renderItems)
                RenderHelper.renderSlotItem(mc.getItemRenderer(), storedItem, slot, slotCount, ms, buffer, light);
            if (count > 0 && renderCounts)
                RenderHelper.renderSlotCount(String.valueOf(count), slot, slotCount, ms, buffer, light);
            if (lockMode && renderIcons)
                RenderHelper.renderSlotMode(DrawerIcons.LOCK, slot, slotCount, ms, buffer, light);
            if (voidMode && renderIcons)
                RenderHelper.renderSlotMode(DrawerIcons.VOID, slot, slotCount, ms, buffer, light);
        }

        ms.popPose();
    }

    public static void renderFromContraptionContext(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.state.getBlock() instanceof DrawerStorageBlock drawer)) return;

        boolean shouldRenderItems = ModConfigs.client().renderItems.get();
        boolean shouldRenderCounts = ModConfigs.client().renderCounts.get();
        boolean shouldRenderIcons = ModConfigs.client().renderIcons.get();
        if (!shouldRenderItems && !shouldRenderCounts && !shouldRenderIcons)
            return;

        int slotCount = drawer.getSlotCount();
        BlockState state = context.state;
        CompoundTag tag = context.blockEntityData;
        if (tag == null || state == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        boolean renderItems = !tag.contains("RenderItems") || tag.getBoolean("RenderItems") && shouldRenderItems;
        boolean renderCounts = !tag.contains("RenderCounts") || tag.getBoolean("RenderCounts") && shouldRenderCounts;
        boolean renderIcons = !tag.contains("RenderIcons") || tag.getBoolean("RenderIcons") && shouldRenderIcons;
        if (!renderItems && !renderCounts && !renderIcons) return;

        double distance = context.position != null
            ? Math.sqrt(player.distanceToSqr(context.position))
            : Math.sqrt(player.distanceToSqr(context.contraption.entity.toGlobalVector(Vec3.atCenterOf(context.localPos), 1f)));

        if (distance >= 10) {
            return;
        }

        Direction facing = state.getValue(HORIZONTAL_FACING);

        BlockPos lightPos = context.contraption.entity.blockPosition().offset(context.localPos).relative(facing);

        int blockLight = context.world.getBrightness(LightLayer.BLOCK, lightPos);
        int skyLight = context.world.getBrightness(LightLayer.SKY, lightPos);
        int light = LightTexture.pack(blockLight, skyLight);

        PoseStack ms = matrices.getModelViewProjection();

        ms.pushPose();
        ms.translate(context.localPos.getX() + 0.5f, context.localPos.getY() + 0.5f, context.localPos.getZ() + 0.5f);
        ms.mulPose(Axis.YP.rotationDegrees(RenderHelper.getFaceRotation(facing)));
        ms.translate(0, 0, 0.47);

        if (tag.contains("Upgrade") && renderIcons) {
            ItemStack upgrade = tag.getCompound("Upgrade").isEmpty()
                ? ItemStack.EMPTY
                : ItemStack.parseOptional(renderWorld.registryAccess(), tag.getCompound("Upgrade"));
            if (!upgrade.isEmpty()) {
                RenderHelper.renderDrawerUpgrade(upgrade, slotCount, ms, buffer, light);
            }
        }

        if (tag.contains("Slots", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Slots", Tag.TAG_COMPOUND);
            for (int slot = 0; slot < Math.min(list.size(), slotCount); slot++) {
                CompoundTag slotTag = list.getCompound(slot);
                if (slotTag.contains("Item") && renderItems) {
                    ItemStack storedItem = slotTag.getCompound("Item").isEmpty()
                        ? ItemStack.EMPTY
                        : ItemStack.parseOptional(renderWorld.registryAccess(), slotTag.getCompound("Item"));
                    if (!storedItem.isEmpty())
                        RenderHelper.renderSlotItem(mc.getItemRenderer(), storedItem, slot, slotCount, ms, buffer, light);
                }
                if (slotTag.contains("Count") && renderCounts) {
                    int count = slotTag.getInt("Count");
                    if (count > 0)
                        RenderHelper.renderSlotCount(String.valueOf(count), slot, slotCount, ms, buffer, light);
                }
                if (slotTag.contains("Locked") && renderIcons) {
                    boolean lockMode = slotTag.getBoolean("Locked");
                    if (lockMode) {
                        RenderHelper.renderSlotMode(DrawerIcons.LOCK, slot, slotCount, ms, buffer, light);
                    }
                }
                if (slotTag.contains("Void") && renderIcons) {
                    boolean voidMode = slotTag.getBoolean("Void");
                    if (voidMode) {
                        RenderHelper.renderSlotMode(DrawerIcons.VOID, slot, slotCount, ms, buffer, light);
                    }
                }
            }
        }

        ms.popPose();
    }
}
