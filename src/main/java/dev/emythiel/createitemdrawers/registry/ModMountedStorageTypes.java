package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.contraption.MountedStorageType;

public class ModMountedStorageTypes {
    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public static final RegistryEntry<MountedItemStorageType<?>, MountedStorageType> MOUNTED_DRAWER =
        REGISTRATE.mountedItemStorage("drawer", MountedStorageType::new)
            .register();

    public static void register() {}
}
