package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class DrawerSneakBypassHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void useOnDrawerSlotIgnoresSneak(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem() != TriState.DEFAULT)
            return;

        // Check if drawer block
        var level = event.getLevel();
        var pos = event.getPos();
        var be = level.getBlockEntity(pos);

        if (!(be instanceof DrawerBlockEntity))
            return;

        // Check if fron face clicked
        var state = level.getBlockState(pos);
        var front = state.getValue(HorizontalDirectionalBlock.FACING);
        var clickedFace = event.getFace();

        if (clickedFace != front)
            return;

        event.setUseBlock(TriState.TRUE);
    }
}
