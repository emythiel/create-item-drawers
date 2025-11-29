package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModItems {
    private static final CreateRegistrate REGISTRATE =
        CreateItemDrawersRegistrate.REGISTRATE;

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T1 =
        REGISTRATE.item("capacity_upgrade_t1",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 1))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('I', Items.IRON_INGOT)
                .define('P', AllItems.PRECISION_MECHANISM)
                .define('C', Items.ENDER_CHEST)
                .pattern("III")
                .pattern("PCP")
                .pattern("III")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T2 =
        REGISTRATE.item("capacity_upgrade_t2",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 2))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('G', Items.GOLD_INGOT)
                .define('U', ModItems.CAPACITY_UPGRADE_T1)
                .pattern("GGG")
                .pattern("U U")
                .pattern("GGG")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T3 =
        REGISTRATE.item("capacity_upgrade_t3",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 3))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('B', AllItems.BRASS_INGOT)
                .define('U', ModItems.CAPACITY_UPGRADE_T2)
                .pattern("BBB")
                .pattern("U U")
                .pattern("BBB")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T4 =
        REGISTRATE.item("capacity_upgrade_t4",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 4))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('D', Items.DIAMOND)
                .define('U', ModItems.CAPACITY_UPGRADE_T3)
                .pattern("DDD")
                .pattern("U U")
                .pattern("DDD")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T5 =
        REGISTRATE.item("capacity_upgrade_t5",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 5))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('N', Items.NETHERITE_INGOT)
                .define('U', ModItems.CAPACITY_UPGRADE_T4)
                .pattern("NNN")
                .pattern("U U")
                .pattern("NNN")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .register();


    public static void register() {}
}
