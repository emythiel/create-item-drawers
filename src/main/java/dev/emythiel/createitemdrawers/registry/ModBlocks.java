package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.util.connection.DrawerCTBehaviour;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    static {
        REGISTRATE.setCreativeTab(ModTabs.CREATIVE_TAB);
    }

    public static final BlockEntry<DrawerBlock> SINGLE_DRAWER =
        REGISTRATE.block("single_drawer", p -> new DrawerBlock(p, 1))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .simpleItem()
            .register();

    public static final BlockEntry<DrawerBlock> DOUBLE_DRAWER =
        REGISTRATE.block("double_drawer", p -> new DrawerBlock(p, 2))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .simpleItem()
            .register();

    public static final BlockEntry<DrawerBlock> QUAD_DRAWER =
        REGISTRATE.block("quad_drawer", p -> new DrawerBlock(p, 4))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .simpleItem()
            .register();

    public static void register() {}
}
