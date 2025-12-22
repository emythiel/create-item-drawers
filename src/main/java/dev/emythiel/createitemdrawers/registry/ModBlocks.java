package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlock;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlockItem;
import dev.emythiel.createitemdrawers.contraption.DrawerMountedMovementBehaviour;
import dev.emythiel.createitemdrawers.util.connection.DrawerCTBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.api.contraption.storage.item.MountedItemStorageType.mountedItemStorage;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ModBlocks {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    static {
        REGISTRATE.setCreativeTab(ModTabs.CREATIVE_TAB);
    }

    public static final BlockEntry<DrawerStorageBlock> DRAWER_SINGLE = REGISTRATE
            .block("item_drawer_single", p -> new DrawerStorageBlock(p, 1))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .tag(ModTags.Blocks.DRAWERS)
            .transform(mountedItemStorage(ModMountedStorageTypes.MOUNTED_DRAWER))
            .onRegister(movementBehaviour(new DrawerMountedMovementBehaviour()))
            .item(DrawerStorageBlockItem::new)
            .tag(ModTags.Items.DRAWERS)
            .build()
            .register();

    public static final BlockEntry<DrawerStorageBlock> DRAWER_DOUBLE = REGISTRATE
            .block("item_drawer_double", p -> new DrawerStorageBlock(p, 2))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .tag(ModTags.Blocks.DRAWERS)
            .transform(mountedItemStorage(ModMountedStorageTypes.MOUNTED_DRAWER))
            .onRegister(movementBehaviour(new DrawerMountedMovementBehaviour()))
            .item(DrawerStorageBlockItem::new)
            .tag(ModTags.Items.DRAWERS)
            .build()
            .register();

    public static final BlockEntry<DrawerStorageBlock> DRAWER_QUAD = REGISTRATE
            .block("item_drawer_quad", p -> new DrawerStorageBlock(p, 4))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200))
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) -> { }) // Disable auto model gen
            .onRegister(CreateRegistrate.connectedTextures(DrawerCTBehaviour::new))
            .tag(ModTags.Blocks.DRAWERS)
            .transform(mountedItemStorage(ModMountedStorageTypes.MOUNTED_DRAWER))
            .onRegister(movementBehaviour(new DrawerMountedMovementBehaviour()))
            .item(DrawerStorageBlockItem::new)
            .tag(ModTags.Items.DRAWERS)
            .build()
            .register();

    public static void register() {}
}
