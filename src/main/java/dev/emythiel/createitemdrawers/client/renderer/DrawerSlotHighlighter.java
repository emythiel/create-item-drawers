package dev.emythiel.createitemdrawers.client.renderer;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

import java.util.ArrayList;
import java.util.List;

public class DrawerSlotHighlighter {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        DrawerBlockEntity be = getDrawer(event);
        if (be == null) return;

        int slot = DrawerInteractionHelper.getHitSlot(be, event.getTarget().getLocation());
        if (slot < 0) return;

        Direction face = event.getTarget().getDirection();
        Direction front = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        if (face != front) return;

        AABB box = DrawerInteractionHelper.getSlotAABB(be, slot);

        Outliner.getInstance()
            .showAABB("drawer_slot_" + be.getBlockPos() + "_" + slot, box)
            .colored(0xFFFFFF)
            .lineWidth(0.02f);

        Player player = Minecraft.getInstance().player;
        boolean holdingWrench = player.getMainHandItem().is(AllItems.WRENCH.get());
        if (holdingWrench) {
            List<MutableComponent> tip = new ArrayList<>();
            tip.add(CreateItemDrawerLang.translate("interaction.settings").component());
            tip.add(CreateItemDrawerLang.translate("interaction.open_settings").component());
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        }
    }

    private static DrawerBlockEntity getDrawer(RenderHighlightEvent.Block event) {
        var level = event.getCamera().getEntity().level();
        var pos = event.getTarget().getBlockPos();

        if (level.getBlockEntity(pos) instanceof DrawerBlockEntity be)
            return be;

        return null;
    }
}
