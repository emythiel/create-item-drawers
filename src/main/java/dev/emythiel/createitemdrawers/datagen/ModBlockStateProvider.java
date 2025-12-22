package dev.emythiel.createitemdrawers.datagen;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends RegistrateBlockstateProvider {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(REGISTRATE, output, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlockWithExistingModel(ModBlocks.DRAWER_SINGLE, "item_drawer_single");
        horizontalBlockWithExistingModel(ModBlocks.DRAWER_DOUBLE, "item_drawer_double");
        horizontalBlockWithExistingModel(ModBlocks.DRAWER_QUAD, "item_drawer_quad");
    }

    private void horizontalBlockWithExistingModel(BlockEntry<?> entry, String modelName) {
        this.horizontalBlock(
            entry.get(),
            models().getExistingFile(modLoc("block/" + modelName))
        );
    }
}
