package dev.emythiel.createitemdrawers.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CapacityUpgradeBase extends Item {

    public CapacityUpgradeBase(Properties properties) {
        super(properties);
    }

    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable(
            "tooltip.create_item_drawers.capacity_base"
        ).withStyle(ChatFormatting.GRAY));
    }*/
}
