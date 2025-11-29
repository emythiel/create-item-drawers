package dev.emythiel.createitemdrawers.datagen;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import dev.emythiel.createitemdrawers.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateItemDrawers.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Blocks.DRAWERS)
            .add(ModBlocks.SINGLE_DRAWER.get())
            .add(ModBlocks.DOUBLE_DRAWER.get())
            .add(ModBlocks.QUAD_DRAWER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.SINGLE_DRAWER.get())
            .add(ModBlocks.DOUBLE_DRAWER.get())
            .add(ModBlocks.QUAD_DRAWER.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(ModBlocks.SINGLE_DRAWER.get())
            .add(ModBlocks.DOUBLE_DRAWER.get())
            .add(ModBlocks.QUAD_DRAWER.get());
    }
}
