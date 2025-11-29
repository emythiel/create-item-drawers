package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.item.CapacityUpgradeItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final CreateRegistrate REGISTRATE =
        CreateItemDrawersRegistrate.REGISTRATE;

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T1 =
        REGISTRATE.item("capacity_upgrade_t1",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 1))
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T2 =
        REGISTRATE.item("capacity_upgrade_t2",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 2))
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T3 =
        REGISTRATE.item("capacity_upgrade_t3",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 3))
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T4 =
        REGISTRATE.item("capacity_upgrade_t4",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 4))
            .register();

    public static final ItemEntry<CapacityUpgradeItem> CAPACITY_UPGRADE_T5 =
        REGISTRATE.item("capacity_upgrade_t5",
            p -> new CapacityUpgradeItem(p.stacksTo(16), 5))
            .register();


    public static void register() {}
}
