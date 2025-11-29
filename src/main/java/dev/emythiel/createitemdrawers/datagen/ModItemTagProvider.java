package dev.emythiel.createitemdrawers.datagen;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import dev.emythiel.createitemdrawers.registry.ModItems;
import dev.emythiel.createitemdrawers.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateItemDrawers.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        tag(ModTags.Items.DRAWERS)
            .add(ModBlocks.SINGLE_DRAWER.get().asItem())
            .add(ModBlocks.DOUBLE_DRAWER.get().asItem())
            .add(ModBlocks.QUAD_DRAWER.get().asItem());

        tag(ModTags.Items.UPGRADES)
            .add(ModItems.CAPACITY_UPGRADE_BASE.get())
            .add(ModItems.CAPACITY_UPGRADE_T1.get())
            .add(ModItems.CAPACITY_UPGRADE_T2.get())
            .add(ModItems.CAPACITY_UPGRADE_T3.get())
            .add(ModItems.CAPACITY_UPGRADE_T4.get())
            .add(ModItems.CAPACITY_UPGRADE_T5.get());
    }
}
