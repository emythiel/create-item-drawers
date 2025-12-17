package dev.emythiel.createitemdrawers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.IntValue ITEM_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue ITEM_RENDER;
    public static ModConfigSpec.IntValue TEXT_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue TEXT_RENDER;
    public static ModConfigSpec.IntValue ADDITIONAL_RENDER_DISTANCE;
    public static ModConfigSpec.BooleanValue ADDITIONAL_RENDER;

    public static ModConfigSpec.BooleanValue SHOW_GOGGLE_TOOLTIP;
    public static ModConfigSpec.BooleanValue GOOGLE_TOOLTIP_REQUIRE_WRENCH;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment().push("render-settings");
        ITEM_RENDER_DISTANCE = builder
            .comment("Sets the distance at which items on the drawers are rendered")
            .defineInRange("itemRenderDistance", 24, 1, 128);
        ITEM_RENDER = builder
            .comment("Global setting to disable rendering of items for all drawers.")
            .define("itemRender", true);
        TEXT_RENDER_DISTANCE = builder
            .comment("Sets the distance at which text on the drawers are rendered")
            .defineInRange("textRenderDistance", 16, 1, 128);
        TEXT_RENDER = builder
            .comment("Global setting to disable rendering of storage counts for all drawers.")
            .define("textRender", true);
        ADDITIONAL_RENDER_DISTANCE = builder
            .comment("Sets the distance at which additional information (such as lock/void mode and upgrades) are rendered")
            .defineInRange("additionalRenderDistance", 4, 1, 128);
        ADDITIONAL_RENDER = builder
            .comment("Global setting to disable rendering of lock/void mode as well as upgrades for all drawers.")
            .define("additionalRender", true);
        builder.pop();

        builder.comment().push("additional-settings");
        SHOW_GOGGLE_TOOLTIP = builder
            .comment("Enable to see drawer information while wearing Engineer's Goggles")
            .define("goggleTooltip", false);
        GOOGLE_TOOLTIP_REQUIRE_WRENCH = builder
            .comment(
                "Enable to require wielding a wrench in addition to wearing Engineer's Goggles to see drawer information.",
                "The tooltip may get in the way a lot if you don't use this depending on your GUI scale and such.")
                .define("goggleTooltipRequireWrench", false);
        builder.pop();

        SPEC = builder.build();
    }
}
