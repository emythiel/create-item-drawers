package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.emythiel.createitemdrawers.block.DrawerBlock;
import net.minecraft.world.level.block.SoundType;

public class ModBlocks {
    private static final CreateRegistrate REGISTRATE =
        CreateItemDrawersRegistrate.REGISTRATE;

    public static final BlockEntry<DrawerBlock> SINGLE_DRAWER =
        REGISTRATE.block("single_drawer", p -> new DrawerBlock(p, 1, 32))
            .properties(p -> p
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .simpleItem()
            .register();

    public static final BlockEntry<DrawerBlock> DOUBLE_DRAWER =
        REGISTRATE.block("double_drawer", p -> new DrawerBlock(p, 2, 16))
            .properties(p -> p
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .simpleItem()
            .register();

    public static final BlockEntry<DrawerBlock> QUAD_DRAWER =
        REGISTRATE.block("quad_drawer", p -> new DrawerBlock(p, 4, 8))
            .properties(p -> p
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .simpleItem()
            .register();

    public static void register() {}
}
