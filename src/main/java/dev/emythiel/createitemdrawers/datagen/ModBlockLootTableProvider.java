package dev.emythiel.createitemdrawers.datagen;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.DRAWER_SINGLE.get());
        dropSelf(ModBlocks.DRAWER_DOUBLE.get());
        dropSelf(ModBlocks.DRAWER_QUAD.get());
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return REGISTRATE
            .getAll(Registries.BLOCK)
            .stream()
            .filter(e -> e.getId().getNamespace().equals(CreateItemDrawers.MODID))
            .map(RegistryEntry::get)
            .toList();
    }
}
