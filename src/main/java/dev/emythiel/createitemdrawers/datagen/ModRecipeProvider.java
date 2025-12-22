package dev.emythiel.createitemdrawers.datagen;

import com.simibubi.create.AllBlocks;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // Single Drawer Recipe
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.DRAWER_SINGLE.get())
            .requires(AllBlocks.ITEM_VAULT)
            .requires(AllBlocks.ITEM_HATCH)
            .unlockedBy("has_vault", has(AllBlocks.ITEM_VAULT))
            .save(recipeOutput);

        // Double Drawer Recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRAWER_DOUBLE.get())
            .pattern("H")
            .pattern("V")
            .pattern("H")
            .define('H', AllBlocks.ITEM_HATCH)
            .define('V', AllBlocks.ITEM_VAULT)
            .unlockedBy("has_vault", has(AllBlocks.ITEM_VAULT))
            .save(recipeOutput);

        // Double Drawer Recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRAWER_QUAD.get())
            .pattern("H H")
            .pattern(" V ")
            .pattern("H H")
            .define('H', AllBlocks.ITEM_HATCH)
            .define('V', AllBlocks.ITEM_VAULT)
            .unlockedBy("has_vault", has(AllBlocks.ITEM_VAULT))
            .save(recipeOutput);
    }
}
