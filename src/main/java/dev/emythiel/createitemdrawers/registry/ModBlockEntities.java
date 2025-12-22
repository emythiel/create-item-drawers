package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.client.renderer.DrawerRenderer;

public class ModBlockEntities {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public static final BlockEntityEntry<DrawerStorageBlockEntity> DRAWER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("drawer_block_entity", DrawerStorageBlockEntity::new)
            .validBlocks(
                ModBlocks.DRAWER_SINGLE,
                ModBlocks.DRAWER_DOUBLE,
                ModBlocks.DRAWER_QUAD
            )
            .renderer(() -> DrawerRenderer::new)
            .register();

    public static void register() {}
}
