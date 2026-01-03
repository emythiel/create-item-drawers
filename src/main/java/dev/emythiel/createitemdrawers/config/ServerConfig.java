package dev.emythiel.createitemdrawers.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ServerConfig extends ConfigBase {

    // Capacity group
    public final ConfigGroup storageCapacity = group(1, "storageCapacity", Comments.storageCapacity);
    public final ConfigInt storageCapacitySingle = i(32, 1, 1024, "storageCapacitySingle",
        Comments.storageCapacitySingle);
    public final ConfigInt storageCapacityDouble = i(16, 1, 1024, "storageCapacityDouble",
        Comments.storageCapacityDouble);
    public final ConfigInt storageCapacityQuad = i(8, 1, 1024, "storageCapacityQuad",
        Comments.storageCapacityQuad);

    // Upgrade group
    public final ConfigGroup upgradeMultiplier = group(1, "upgradeMultiplier", Comments.upgradeMultiplier);
    public final ConfigInt upgradeT1 = i(2, 1, 1024, "upgradeT1", Comments.upgradeT1);
    public final ConfigInt upgradeT2 = i(4, 1, 1024, "upgradeT2", Comments.upgradeT2);
    public final ConfigInt upgradeT3 = i(8, 1, 1024, "upgradeT3", Comments.upgradeT3);
    public final ConfigInt upgradeT4 = i(16, 1, 1024, "upgradeT4", Comments.upgradeT4);
    public final ConfigInt upgradeT5 = i(32, 1, 1024, "upgradeT5", Comments.upgradeT5);

    @Override
    @NotNull
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String storageCapacity = "Base Item Drawer capacity settings.";
        static String storageCapacitySingle = "Base storage capacity (in stacks) of a Single slotted Item Drawer.";
        static String storageCapacityDouble = "Base storage capacity (in stacks) of a Double slotted Item Drawer.";
        static String storageCapacityQuad = "Base storage capacity (in stacks) of a Quad slotted Item Drawer.";

        static String upgradeMultiplier = "Upgrade multiplier settings";
        static String upgradeT1 = "Capacity multiplier for the Tier I upgrade.";
        static String upgradeT2 = "Capacity multiplier for the Tier II upgrade.";
        static String upgradeT3 = "Capacity multiplier for the Tier III upgrade.";
        static String upgradeT4 = "Capacity multiplier for the Tier IV upgrade.";
        static String upgradeT5 = "Capacity multiplier for the Tier V upgrade.";
    }
}
