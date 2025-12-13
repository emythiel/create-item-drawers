package dev.emythiel.createitemdrawers.item;

import dev.emythiel.createitemdrawers.config.ServerConfig;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CapacityUpgradeItem extends Item {

    private final int tier; // 0-5

    public CapacityUpgradeItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        Component translated = CreateItemDrawerLang.translate(
            "tooltip.capacity_multiplier",
            getTierMultiplier()
        ).component();

        List<Component> formattedLines = FontHelper.cutTextComponent(
            translated,
            FontHelper.Palette.STANDARD_CREATE
        );

        tooltip.addAll(formattedLines);
    }

    public int getTierMultiplier() {
        return switch (tier) {
            case 1 -> ServerConfig.CAPACITY_UPGRADE_T1.get();
            case 2 -> ServerConfig.CAPACITY_UPGRADE_T2.get();
            case 3 -> ServerConfig.CAPACITY_UPGRADE_T3.get();
            case 4 -> ServerConfig.CAPACITY_UPGRADE_T4.get();
            case 5 -> ServerConfig.CAPACITY_UPGRADE_T5.get();
            default -> 1;
        };
    }
}
