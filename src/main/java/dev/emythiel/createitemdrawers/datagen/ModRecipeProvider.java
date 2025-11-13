package dev.emythiel.createitemdrawers.datagen;

import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        Item vault = getCreateItem("item_vault");
        Item hatch = getCreateItem("item_hatch");

        // Single Drawer Recipe
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.SINGLE_DRAWER.get())
            .requires(vault)
            .requires(hatch)
            .unlockedBy("has_vault", has(vault))
            .save(recipeOutput);

        // Double Drawer Recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DOUBLE_DRAWER.get())
            .pattern("H")
            .pattern("V")
            .pattern("H")
            .define('H', hatch)
            .define('V', vault)
            .unlockedBy("has_vault", has(vault))
            .save(recipeOutput);

        // Double Drawer Recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QUAD_DRAWER.get())
            .pattern("H H")
            .pattern(" V ")
            .pattern("H H")
            .define('H', hatch)
            .define('V', vault)
            .unlockedBy("has_vault", has(vault))
            .save(recipeOutput);
    }

    private static Item getCreateItem(String name) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", name));
    }
}
