package dev.emythiel.createitemdrawers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.IntValue ITEM_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue ITEM_RENDER;
    public static ModConfigSpec.IntValue COUNT_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue COUNT_RENDER;
    public static ModConfigSpec.IntValue ADDITIONAL_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue ADDITIONAL_RENDER;

    public static ModConfigSpec.BooleanValue GOGGLE_TOOLTIP;
    public static ModConfigSpec.BooleanValue GOOGLE_TOOLTIP_REQUIRE_WRENCH;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment().push("render_settings");
        ITEM_RENDER_DISTANCE = builder
            .comment("Sets the distance at which items on the drawers are rendered.")
            .defineInRange("item_render_distance", 24, 1, 128);
        ITEM_RENDER = builder
            .comment("Global setting to disable rendering of items for all drawers.")
            .define("item_render", true);
        COUNT_RENDER_DISTANCE = builder
            .comment("Sets the distance at which text on the drawers are rendered.")
            .defineInRange("count_render_distance", 16, 1, 128);
        COUNT_RENDER = builder
            .comment("Global setting to disable rendering of storage counts for all drawers.")
            .define("count_render", true);
        ADDITIONAL_RENDER_DISTANCE = builder
            .comment("Sets the distance at which additional information (such as lock/void mode and upgrades) are rendered.")
            .defineInRange("additional_render_distance", 4, 1, 128);
        ADDITIONAL_RENDER = builder
            .comment("Global setting to disable rendering of lock/void mode as well as upgrades for all drawers.")
            .define("additional_render", true);
        builder.pop();

        builder.comment().push("additional_settings");
        GOGGLE_TOOLTIP = builder
            .comment("Enable to see drawer information while wearing Engineer's Goggles.")
            .define("goggle_tooltip", false);
        GOOGLE_TOOLTIP_REQUIRE_WRENCH = builder
            .comment(
                "Enable to require wielding a wrench in addition to wearing Engineer's Goggles to see drawer information.",
                "If you find the goggle tooltip gets in the way a lot, you can enable this to prevent it from constantly showing, but still being available when needed.")
            .define("goggle_tooltip_require_wrench", false);
        builder.pop();

        SPEC = builder.build();
    }
}
