package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeBase;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModItems {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    static {
        REGISTRATE.setCreativeTab(ModTabs.CREATIVE_TAB);
    }

    public static final ItemEntry<CapacityUpgradeBase> CAPACITY_UPGRADE_BASE =
        REGISTRATE.item("capacity_upgrade_base",
                p -> new CapacityUpgradeBase(p.stacksTo(16)))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('V', AllBlocks.ITEM_VAULT)
                .define('B', AllItems.CRAFTING_BLUEPRINT)
                .pattern(" V ")
                .pattern("VBV")
                .pattern(" V ")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T1 =
        REGISTRATE.item("capacity_upgrade_t1",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 1))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('A', AllItems.ANDESITE_ALLOY)
                .define('U', ModItems.CAPACITY_UPGRADE_BASE)
                .pattern("AAA")
                .pattern("U U")
                .pattern("AAA")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T2 =
        REGISTRATE.item("capacity_upgrade_t2",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 2))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('B', AllItems.BRASS_INGOT)
                .define('U', ModItems.CAPACITY_UPGRADE_T1)
                .define('P', AllItems.PRECISION_MECHANISM)
                .pattern("BBB")
                .pattern("UPU")
                .pattern("BBB")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T3 =
        REGISTRATE.item("capacity_upgrade_t3",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 3))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('D', Items.DIAMOND)
                .define('U', ModItems.CAPACITY_UPGRADE_T2)
                .define('S', AllItems.STURDY_SHEET)
                .pattern("DDD")
                .pattern("USU")
                .pattern("DDD")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T4 =
        REGISTRATE.item("capacity_upgrade_t4",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 4))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('N', Items.NETHERITE_INGOT)
                .define('U', ModItems.CAPACITY_UPGRADE_T3)
                .define('E', AllBlocks.EXPERIENCE_BLOCK)
                .pattern("NNN")
                .pattern("UEU")
                .pattern("NNN")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T5 =
        REGISTRATE.item("capacity_upgrade_t5",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 5))
            .recipe((ctx, prov) -> ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ctx.get(), 1)
                .define('S', Items.SHULKER_SHELL)
                .define('U', ModItems.CAPACITY_UPGRADE_T4)
                .define('H', Items.HEAVY_CORE)
                .pattern("SSS")
                .pattern("UHU")
                .pattern("SSS")
                .group("capacity_upgrade")
                .unlockedBy("has_item_drawer", RegistrateRecipeProvider.has(ModTags.Items.DRAWERS))
                .save(prov, ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, "crafting_upgrade/" + ctx.getName()))
            )
            .tag(ModTags.Items.UPGRADES)
            .register();


    public static void register() {}
}
