package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.contraption.DrawerMountedStorageType;

public class ModMountedStorageTypes {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public static final RegistryEntry<MountedItemStorageType<?>, DrawerMountedStorageType> MOUNTED_DRAWER =
        REGISTRATE.mountedItemStorage("drawer", DrawerMountedStorageType::new)
            .register();

    public static void register() {}
}
