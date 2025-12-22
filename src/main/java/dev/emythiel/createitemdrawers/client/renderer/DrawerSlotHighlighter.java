package dev.emythiel.createitemdrawers.client.renderer;

import com.simibubi.create.AllSpecialTextures;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

public class DrawerSlotHighlighter {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        DrawerStorageBlockEntity be = getDrawer(event);
        if (be == null) return;

        int slot = DrawerInteractionHelper.getHitSlot(be, event.getTarget().getLocation());
        if (slot < 0) return;

        Direction face = event.getTarget().getDirection();
        Direction front = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        if (face != front) return;

        AABB box = DrawerInteractionHelper.getSlotAABB(be, slot);

        // See if we're over capacity (render red warning instead)
        int capacity = be.getStorage().getCapacity(slot, be.getStorage().getSlot(slot).getStoredItem());
        boolean overCapacity = capacity >= be.getStorage().getSlot(slot).getCount();

        Outliner.getInstance()
            .showAABB("drawer_slot_" + be.getBlockPos() + "_" + slot, box)
            .colored(overCapacity ? 0xFFFFFF : 0xFF0000)
            .withFaceTexture(overCapacity ? null : AllSpecialTextures.SELECTION)
            .lineWidth(0.01f);
    }

    private static DrawerStorageBlockEntity getDrawer(RenderHighlightEvent.Block event) {
        var level = event.getCamera().getEntity().level();
        var pos = event.getTarget().getBlockPos();

        if (level.getBlockEntity(pos) instanceof DrawerStorageBlockEntity be)
            return be;

        return null;
    }
}
