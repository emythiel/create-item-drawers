package dev.emythiel.createitemdrawers.datagen;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.CreateItemDrawersRegistrate;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.SINGLE_DRAWER.get());
        dropSelf(ModBlocks.DOUBLE_DRAWER.get());
        dropSelf(ModBlocks.QUAD_DRAWER.get());
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return CreateItemDrawersRegistrate.REGISTRATE
            .getAll(Registries.BLOCK)
            .stream()
            .filter(e -> e.getId().getNamespace().equals(CreateItemDrawers.MODID))
            .map(RegistryEntry::get)
            .toList();
    }
}
