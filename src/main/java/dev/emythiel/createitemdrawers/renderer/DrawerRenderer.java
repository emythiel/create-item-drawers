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
    public DrawerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    protected void renderSafe(DrawerStorageBlockEntity be, float partialTicks,
                              PoseStack ms, MultiBufferSource buffer, int packedLight, int overlay) {

        boolean shouldRenderItems = ModConfigs.client().renderItems.get();
        int itemDist = ModConfigs.client().renderItemsDistance.get();
        boolean shouldRenderCounts = ModConfigs.client().renderCounts.get();
        int countDist = ModConfigs.client().renderCountsDistance.get();
        boolean shouldRenderIcons = ModConfigs.client().renderIcons.get();
        int iconDist = ModConfigs.client().renderIconsDistance.get();

        boolean isPonderScene = be.getLevel() instanceof PonderLevel;

        if (!shouldRenderItems && !shouldRenderCounts && !shouldRenderIcons && !isPonderScene)
            return; // All renders disabled and not ponder, just exit

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // Check player distance
        double distSq = player.distanceToSqr(
            be.getBlockPos().getX() + 0.5,
            be.getBlockPos().getY() + 0.5,
            be.getBlockPos().getZ() + 0.5
        );

        boolean renderItems = be.getRenderItems() && distSq <= itemDist * itemDist && shouldRenderItems;
        boolean renderCounts = be.getRenderCounts() && distSq <= countDist * countDist && shouldRenderCounts;
        boolean renderIcons = be.getRenderIcons() && distSq <= iconDist * iconDist && shouldRenderIcons;

        // If ponder scene, overwrite to force render
        if (isPonderScene) {
            renderItems = true;
            renderCounts = true;
            renderIcons = true;
        }

        if (!renderItems && !renderCounts && !renderIcons)
            return;

        // Check if player is in front of block (don't render if behind)
        Direction facing = be.getBlockState().getValue(HORIZONTAL_FACING);

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

        if (frontNormal.dot(toPlayer) <= 0 && !isPonderScene)
            return;

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
                RenderHelper.renderSlotMode(RenderHelper.DrawerIcon.LOCK, slot, slotCount, ms, buffer, light);
            if (voidMode && renderIcons)
                RenderHelper.renderSlotMode(RenderHelper.DrawerIcon.VOID, slot, slotCount, ms, buffer, light);
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

        // Checking if player is in front not reliable on contraptions, at least not with same method as normal render
        /*Vec3 frontNormal = new Vec3(
            facing.getStepX(),
            facing.getStepY(),
            facing.getStepZ()
        ).normalize();

        Vec3 toPlayer = new Vec3(
            player.getX() - (context.localPos.getX() + 0.5),
            player.getY() - (context.localPos.getY() + 0.5),
            player.getZ() - (context.localPos.getZ() + 0.5)
        ).normalize();

        if (frontNormal.dot(toPlayer) <= 0)
            return;*/

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
                        RenderHelper.renderSlotMode(RenderHelper.DrawerIcon.LOCK, slot, slotCount, ms, buffer, light);
                    }
                }
                if (slotTag.contains("Void") && renderIcons) {
                    boolean voidMode = slotTag.getBoolean("Void");
                    if (voidMode) {
                        RenderHelper.renderSlotMode(RenderHelper.DrawerIcon.VOID, slot, slotCount, ms, buffer, light);
                    }
                }
            }
        }

        ms.popPose();
    }
}
