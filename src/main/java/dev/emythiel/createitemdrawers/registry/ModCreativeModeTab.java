package dev.emythiel.createitemdrawers.registry;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateItemDrawers.MODID);

    public static final ResourceKey<CreativeModeTab> ITEM_DRAWERS_TAB_KEY =
        ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "createitemdrawers_tab")
        );

    public static final Supplier<CreativeModeTab> ITEM_DRAWERS_TAB = TABS.register(
        "createitemdrawers_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.create_item_drawers"))
            .icon(() -> new ItemStack(ModBlocks.SINGLE_DRAWER.asItem()))
            .build()
    );

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
