package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CreateItemDrawers.MODID)
public class DrawerExtractionHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        var level = event.getLevel();
        var pos = event.getPos();
        var be = level.getBlockEntity(pos);

        if (!(be instanceof DrawerBlockEntity drawer))
            return;

        var state = drawer.getBlockState();
        Direction front = state.getValue(HorizontalDirectionalBlock.FACING);

        if (event.getFace() != front)
            return;

        event.setCanceled(true);  // Prevent block breaking

        if (level.isClientSide())
            return;

        Player player = event.getEntity();
        boolean sneaking = player.isShiftKeyDown();

        BlockHitResult hit = (BlockHitResult) player.pick(5.0D, 0.0F, false);
        if (hit == null || !hit.getBlockPos().equals(pos))
            return;

        int slot = DrawerInteractionHelper.getHitSlot(drawer, hit.getLocation());
        if (slot < 0)
            return;

        extractFromSlot(drawer, slot, player, sneaking, level, pos);
    }

    private static void extractFromSlot(DrawerBlockEntity drawer, int slot, Player player, boolean sneaking, Level level, BlockPos pos) {
        var storage = drawer.getStorage();
        var drawerSlot = storage.getSlot(slot);

        if (drawerSlot.isEmpty())
            return;

        int amount = sneaking ? drawerSlot.getStoredItem().getMaxStackSize() : 1;

        ItemStack extracted = storage.extract(slot, amount, false);
        if (extracted.isEmpty())
            return;

        player.getInventory().placeItemBackInInventory(extracted);

        /*CreateItemDrawerLang.translate(sneaking ? "interaction.extract_stack" : "interaction.extract_one")
            .sendStatus(player);*/
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, 0.2f);
        drawer.setChangedAndSync();
    }
}
