package dev.emythiel.createitemdrawers.util;

import dev.emythiel.createitemdrawers.block.DrawerStorageBlockItem;
import dev.emythiel.createitemdrawers.registry.ModConfigs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Applies slowness debuff to players holding filled drawers, if enabled in config.
 * Inspired by Storage Drawers implementation.
 */
public class DrawerSlownessDebuff {

    public static void tick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.tickCount % 60 != 0) return;
        if (!ModConfigs.server().slowness.get()) return;
        if (!ModConfigs.server().slownessCreative.get() && player.isCreative()) return;

        Inventory inv = event.getEntity().getInventory();
        int drawerCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (drawerHasData(stack))
                drawerCount += stack.getCount();
        }

        applyScaledSlowness(player, drawerCount);
    }

    private static boolean drawerHasData(ItemStack stack) {
        if (!(stack.getItem() instanceof DrawerStorageBlockItem))
            return false;

        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null)
            return false;

        CompoundTag tag = customData.copyTag();
        return tag.contains("Slots");
    }

    private static void applyScaledSlowness(Player player, int drawerCount) {
        int slownessAmount = ModConfigs.server().slownessAmount.get();

        if (drawerCount <= slownessAmount)
            return;

        int excess = drawerCount - slownessAmount;
        int slownessLevel = (excess - 1) / slownessAmount;
        slownessLevel = Math.min(slownessLevel, 3);  // Cap at slowness IV (amplifier 3)

        player.addEffect(new MobEffectInstance(
            MobEffects.MOVEMENT_SLOWDOWN,
            100,
            slownessLevel,
            true,
            true,
            true
        ));
    }
}
