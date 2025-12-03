package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;

public class ModBlockEntities {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public static final BlockEntityEntry<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("drawer_block_entity", DrawerBlockEntity::new)
            .validBlocks(
                ModBlocks.SINGLE_DRAWER,
                ModBlocks.DOUBLE_DRAWER,
                ModBlocks.QUAD_DRAWER
            )
            // TODO:
            //.renderer(() -> DrawerRenderer::new)
            //.visual(() -> DrawerVisual::new)
            .register();

    public static void register() {}
}
