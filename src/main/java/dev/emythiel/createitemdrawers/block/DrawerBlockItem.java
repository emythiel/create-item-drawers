package dev.emythiel.createitemdrawers.block;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class DrawerBlockItem extends BlockItem {

    public DrawerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        // Check if there's any nbt data stored
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag beTag = customData.copyTag();

        if (beTag.contains("Slots")) {
            ListTag slots = beTag.getList("Slots", ListTag.TAG_COMPOUND);

            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.create_item_drawers.storage")
                .withStyle(FontHelper.styleFromColor(0x5391e1)));

            for (int i = 0; i < slots.size(); i++) {
                CompoundTag slotTag = slots.getCompound(i);
                ItemStack storedItem = ItemStack.parseOptional(Objects.requireNonNull(context.registries()), slotTag.getCompound("Item"));
                int count = slotTag.getInt("Count");

                MutableComponent slotLine = Component.literal(" ");

                if (!storedItem.isEmpty() && count > 0) {
                    // If slot is not empty
                    slotLine = slotLine
                        .append(storedItem.getHoverName())
                        .append(Component.literal(" x" + count));
                    tooltipComponents.add(slotLine.withStyle(FontHelper.styleFromColor(0x96b7e0)));
                } else {
                    // If slot is empty
                    slotLine = slotLine.append(Component.translatable("tooltip.create_item_drawers.empty"));
                    tooltipComponents.add(slotLine.withStyle(ChatFormatting.DARK_GRAY));
                }
            }

            if (beTag.contains("Upgrade")) {
                tooltipComponents.add(Component.translatable("tooltip.create_item_drawers.upgrade")
                    .withStyle(FontHelper.styleFromColor(0x5391e1)));

                ItemStack upgradeItem = ItemStack.parseOptional(Objects.requireNonNull(context.registries()), beTag.getCompound("Upgrade"));

                MutableComponent upgradeLine = Component.literal(" ");

                if (!upgradeItem.isEmpty()) {
                    // If upgrade is not empty
                    upgradeLine = upgradeLine.append(upgradeItem.getHoverName());
                    tooltipComponents.add(upgradeLine.withStyle(FontHelper.styleFromColor(0x96b7e0)));
                } else {
                    // If upgrade is empty (shouldn't get here, but just in case)
                    upgradeLine = upgradeLine.append(Component.translatable("tooltip.create_item_drawers.empty"));
                    tooltipComponents.add(upgradeLine.withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }
    }
}
