package dev.emythiel.createitemdrawers.block;

import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {

        super.appendHoverText(stack, context, tooltip, tooltipFlag);

        // Check if there's any nbt data stored
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) {
            return;
        }

        tooltip.add(CreateItemDrawerLang.translate("tooltip.hold_for_contents",
            (Screen.hasControlDown()) ? "§fCtrl" : "§7Ctrl").component().withStyle(ChatFormatting.DARK_GRAY));
        if (!Screen.hasControlDown())
            return;

        CompoundTag beTag = customData.copyTag();

        if (beTag.contains("Upgrade")) {
            tooltip.add(CreateItemDrawerLang.translate("tooltip.upgrade").component()
                .withStyle(FontHelper.styleFromColor(0x5391e1)));

            ItemStack upgradeItem = ItemStack.parseOptional(Objects.requireNonNull(context.registries()), beTag.getCompound("Upgrade"));

            MutableComponent upgradeLine = Component.literal(" ");

            if (!upgradeItem.isEmpty()) {
                upgradeLine = upgradeLine.append(upgradeItem.getHoverName());
                tooltip.add(upgradeLine.withStyle(FontHelper.styleFromColor(0x96b7e0)));
            } else {
                upgradeLine = upgradeLine.append(CreateItemDrawerLang.translate("tooltip.empty").component());
                tooltip.add(upgradeLine.withStyle(ChatFormatting.DARK_GRAY));
            }
        }


        if (beTag.contains("Slots")) {
            tooltip.add(Component.literal(""));
            tooltip.add(CreateItemDrawerLang.translate("tooltip.storage").component()
                .withStyle(FontHelper.styleFromColor(0x5391e1)));

            ListTag slots = beTag.getList("Slots", ListTag.TAG_COMPOUND);

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
                    tooltip.add(slotLine.withStyle(FontHelper.styleFromColor(0x96b7e0)));
                } else {
                    // If slot is empty
                    slotLine = slotLine.append(CreateItemDrawerLang.translate("tooltip.empty").component());
                    tooltip.add(slotLine.withStyle(ChatFormatting.DARK_GRAY));
                }


                if (slotTag.contains("Locked") && slotTag.contains("Void")) {
                    boolean locked = slotTag.getBoolean("Locked");
                    boolean voiding = slotTag.getBoolean("Void");

                    MutableComponent modeLine = Component.literal(("  ⤷["))
                        .withStyle(ChatFormatting.DARK_GRAY);

                    MutableComponent lockedText = CreateItemDrawerLang.translate("tooltip.lock").component();
                    if (locked) {
                        lockedText.withStyle(FontHelper.styleFromColor(0x96b7e0));
                    } else {
                        lockedText.withStyle(ChatFormatting.DARK_GRAY);
                    }
                    modeLine = modeLine.append(lockedText);

                    modeLine = modeLine.append(Component.literal("|").withStyle(ChatFormatting.DARK_GRAY));

                    MutableComponent voidingText = CreateItemDrawerLang.translate("tooltip.void").component();
                    if (voiding) {
                        voidingText.withStyle(FontHelper.styleFromColor(0x96b7e0));
                    } else {
                        voidingText.withStyle(ChatFormatting.DARK_GRAY);
                    }
                    modeLine = modeLine.append(voidingText);

                    modeLine = modeLine.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));

                    tooltip.add(modeLine);
                }
            }
        }
    }
}
