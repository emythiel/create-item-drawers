package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class DrawerExtractionHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (!(level.getBlockEntity(pos) instanceof DrawerStorageBlockEntity drawerBE))
            return;

        BlockState state = level.getBlockState(pos);
        Direction front = state.getValue(HorizontalDirectionalBlock.FACING);

        if (event.getFace() != front)
            return;

        // Prevent block breaking
        event.setCanceled(true);

        if (level.isClientSide())
            return;

        BlockHitResult hit = (BlockHitResult) player.pick(5.0D, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos))
            return;

        int slot = DrawerInteractionHelper.getHitSlot(drawerBE, hit.getLocation());
        if (slot < 0)
            return;

        drawerBE.handleLeftClick(player, slot);
    }
}
