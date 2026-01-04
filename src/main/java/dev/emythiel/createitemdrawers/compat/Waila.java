package dev.emythiel.createitemdrawers.compat;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.storage.DrawerStorage;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

@WailaPlugin
public class Waila implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(CreateItemDrawers.asResource("display.upgrade"), true);
        registration.addConfig(CreateItemDrawers.asResource("display.empty"), true);

        WailaDrawer provider = new WailaDrawer();
        registration.registerBlockComponent(provider, DrawerStorageBlock.class);
    }

    private static class WailaDrawer implements IBlockComponentProvider {

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            DrawerStorageBlockEntity drawerBE = (DrawerStorageBlockEntity) accessor.getBlockEntity();
            if (!(drawerBE instanceof DrawerStorageBlockEntity)) return;

            // Remove the default item storage reader, so we can replace with our own
            // This is to prevent reading the entire connected network when looking at a drawer
            tooltip.remove(ResourceLocation.parse("minecraft:item_storage"));

            boolean showUpgrade = config.get(CreateItemDrawers.asResource("display.upgrade"));
            boolean showEmpty = config.get(CreateItemDrawers.asResource("display.empty"));

            ItemStack upgrade = drawerBE.getUpgrade();
            if (!upgrade.isEmpty() && showUpgrade) {
                IElementHelper elements = IElementHelper.get();
                IElement icon = elements.item(new ItemStack(upgrade.getItem()), 0.5f)
                    .size(new Vec2(10, 10))
                    .translate(new Vec2(0, -1));

                tooltip.add(icon);
                tooltip.append(upgrade.getHoverName());
            }

            DrawerStorage storage = drawerBE.getStorage();
            for (int i = 0; i < storage.getSlotCount(); i++) {
                DrawerSlot slot = storage.getSlot(i);

                if (slot.getStoredItem().isEmpty()) {
                    if (showEmpty)
                        tooltip.add(CreateItemDrawerLang.translate("compat.waila.empty").component());
                    continue;
                }

                IElementHelper elements = IElementHelper.get();
                IElement icon = elements.item(new ItemStack(slot.getStoredItem().getItem()), 0.5f)
                    .size(new Vec2(10, 8))
                    .translate(new Vec2(0, -1));
                tooltip.add(icon);

                int count = slot.getCount();
                tooltip.append(Component.nullToEmpty(count + "x "));
                tooltip.append(slot.getStoredItem().getHoverName());
            }
        }

        @Override
        public int getDefaultPriority() {
            return 4999;  // From Functional Storage, thanks!
        }

        @Override
        public ResourceLocation getUid() {
            return CreateItemDrawers.asResource("main");
        }
    }
}
