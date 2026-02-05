package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import dev.emythiel.createitemdrawers.storage.DrawerUnpackingHandler;

@SuppressWarnings("UnstableApiUsage")
public class ModUnpackers {

    public static void register() {
        UnpackingHandler.REGISTRY.register(
            ModBlocks.DRAWER_SINGLE.get(),
            DrawerUnpackingHandler.INSTANCE
        );
        UnpackingHandler.REGISTRY.register(
            ModBlocks.DRAWER_DOUBLE.get(),
            DrawerUnpackingHandler.INSTANCE
        );
        UnpackingHandler.REGISTRY.register(
            ModBlocks.DRAWER_QUAD.get(),
            DrawerUnpackingHandler.INSTANCE
        );
    }
}
