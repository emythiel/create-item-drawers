package dev.emythiel.createitemdrawers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.IntValue ITEM_RENDER_DISTANCE;
    public static ModConfigSpec.IntValue TEXT_RENDER_DISTANCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment().push("render-settings");
        ITEM_RENDER_DISTANCE = builder
            .comment("Sets the distance at which items on the drawers are rendered")
            .defineInRange("itemRenderDistance", 24, 1, 128);
        TEXT_RENDER_DISTANCE = builder
            .comment("Sets the distance at which text on the drawers are rendered")
            .defineInRange("textRenderDistance", 16, 1, 128);
        builder.pop();

        SPEC = builder.build();
    }
}
