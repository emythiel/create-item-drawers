package dev.emythiel.createitemdrawers.datagen;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.emythiel.createitemdrawers.registry.CreateItemDrawersRegistrate;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends RegistrateBlockstateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(CreateItemDrawersRegistrate.REGISTRATE, output, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlockWithExistingModel(ModBlocks.SINGLE_DRAWER, "single_drawer");
        horizontalBlockWithExistingModel(ModBlocks.DOUBLE_DRAWER, "double_drawer");
        horizontalBlockWithExistingModel(ModBlocks.QUAD_DRAWER, "quad_drawer");
    }

    private void horizontalBlockWithExistingModel(BlockEntry<?> entry, String modelName) {
        this.horizontalBlock(
            entry.get(),
            models().getExistingFile(modLoc("block/" + modelName))
        );
    }
}
