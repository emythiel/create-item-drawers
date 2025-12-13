package dev.emythiel.createitemdrawers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ServerConfig {

    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.IntValue SINGLE_CAPACITY;
    public static ModConfigSpec.IntValue DOUBLE_CAPACITY;
    public static ModConfigSpec.IntValue QUAD_CAPACITY;

    public static ModConfigSpec.IntValue CAPACITY_UPGRADE_T1;
    public static ModConfigSpec.IntValue CAPACITY_UPGRADE_T2;
    public static ModConfigSpec.IntValue CAPACITY_UPGRADE_T3;
    public static ModConfigSpec.IntValue CAPACITY_UPGRADE_T4;
    public static ModConfigSpec.IntValue CAPACITY_UPGRADE_T5;

    public static ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
            "Storage capacity is based on stacks per slot.",
            "Upgrades multiplies the base capacity stacks."
        ).push("storage-settings");
        SINGLE_CAPACITY = builder
            .comment("Single slot drawers")
            .defineInRange("singleCapacity", 32, 1, 65536);
        DOUBLE_CAPACITY = builder
            .comment("Double slot drawers")
            .defineInRange("doubleCapacity", 16, 1, 65536);
        QUAD_CAPACITY = builder
            .comment("Quad slot drawers")
            .defineInRange("quadCapacity", 8, 1, 65536);

        CAPACITY_UPGRADE_T1 = builder
            .comment("Tier 1 capacity multiplier")
            .defineInRange("capacityUpgradeT1", 2, 1, 65536);
        CAPACITY_UPGRADE_T2 = builder
            .comment("Tier 2 capacity multiplier")
            .defineInRange("capacityUpgradeT2", 4, 1, 65536);
        CAPACITY_UPGRADE_T3 = builder
            .comment("Tier 3 capacity multiplier")
            .defineInRange("capacityUpgradeT3", 8, 1, 65536);
        CAPACITY_UPGRADE_T4 = builder
            .comment("Tier 4 capacity multiplier")
            .defineInRange("capacityUpgradeT4", 16, 1, 65536);
        CAPACITY_UPGRADE_T5 = builder
            .comment("Tier 5 capacity multiplier")
            .defineInRange("capacityUpgradeT5", 32, 1, 65536);
        builder.pop();

        BLACKLIST = builder
            .comment(
                "List of item IDs that cannot be stored in drawers",
                "Example: [\"minecraft:diamond_sword\", \"minecraft:stone\"]"
            )
            .defineListAllowEmpty(
                "blacklist",
                List.of("create_item_drawers:single_drawer",
                        "create_item_drawers:double_drawer",
                        "create_item_drawers:quad_drawer"
                ), () -> "",
                ServerConfig::validateItemName);

        SPEC = builder.build();
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static boolean isBlacklisted(ResourceLocation id) {
        return BLACKLIST.get().contains(id.toString());
    }
}



/*

package dev.emythiel.createitemdrawers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}


 */
