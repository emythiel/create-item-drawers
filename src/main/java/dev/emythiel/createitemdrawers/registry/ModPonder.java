package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.ponder.DrawerScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonder {
    public static final ResourceLocation CREATE_ITEM_DRAWERS = CreateItemDrawers.asResource("drawers");

    public static class Tags {
        public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
            PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

            helper.registerTag(CREATE_ITEM_DRAWERS)
                .addToIndex()
                .item(ModBlocks.DRAWER_DOUBLE.get(), true, false)
                .title("Create: Item Drawers")
                .description("Items and components related to Create: Item Drawers")
                .register();

            HELPER.addToTag(CREATE_ITEM_DRAWERS)
                .add(ModBlocks.DRAWER_SINGLE)
                .add(ModBlocks.DRAWER_DOUBLE)
                .add(ModBlocks.DRAWER_QUAD);

            HELPER.addToTag(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS)
                .add(ModBlocks.DRAWER_SINGLE)
                .add(ModBlocks.DRAWER_DOUBLE)
                .add(ModBlocks.DRAWER_QUAD);
        }
    }

    public static class Scenes {
        public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
            PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

            HELPER.forComponents(
                ModBlocks.DRAWER_SINGLE,
                ModBlocks.DRAWER_DOUBLE,
                ModBlocks.DRAWER_QUAD
            )
                .addStoryBoard("drawer/intro", DrawerScenes::intro, CREATE_ITEM_DRAWERS)
                .addStoryBoard("drawer/connecting", DrawerScenes::connecting, CREATE_ITEM_DRAWERS);
        }
    }
}
