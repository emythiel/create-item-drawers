package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class ModTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateItemDrawers.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB =
        REGISTER.register("creative_mode_tab", () -> CreativeModeTab.builder()
            .title(CreateItemDrawerLang.translate("creative_tab").component())
            .icon(ModBlocks.DOUBLE_DRAWER::asStack)
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getId())
            .displayItems(new RegistrateDisplayItemGenerator())
            .build());

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static class RegistrateDisplayItemGenerator implements DisplayItemsGenerator {
        private static final Map<Item, Function<Item, ItemStack>> ITEM_FACTORIES = Map.of();

        private static Function<Item, ItemStack> stackFunc() {
            return item -> ITEM_FACTORIES.getOrDefault(item, ItemStack::new).apply(item);
        }

        private static final Map<Item, CreativeModeTab.TabVisibility> ITEM_VISIBILITIES = Map.of();

        private static Function<Item, CreativeModeTab.TabVisibility> visibilityFunc() {
            return item -> ITEM_VISIBILITIES.getOrDefault(item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        @Override
        public void accept(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.@NotNull Output output) {
            List<Item> items = new ArrayList<>(collectBlocks());
            items.addAll(collectItems());

            outputAll(output, items);
        }

        private List<Item> collectBlocks() {
            return CreateItemDrawers.registrate().getAll(Registries.BLOCK).stream()
                .filter(entry -> CreateRegistrate.isInCreativeTab(entry, CREATIVE_TAB))
                .map(entry -> entry.get().asItem())
                .filter(item -> item != Items.AIR)
                .distinct()
                .toList();
        }

        private List<Item> collectItems() {
            return CreateItemDrawers.registrate().getAll(Registries.ITEM).stream()
                .filter(entry -> CreateRegistrate.isInCreativeTab(entry, CREATIVE_TAB))
                .map(RegistryEntry::get)
                .filter(item -> !(item instanceof BlockItem))
                .distinct()
                .toList();
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items) {
            items.forEach(item -> output.accept(stackFunc().apply(item), visibilityFunc().apply(item)));
        }
    }
}
