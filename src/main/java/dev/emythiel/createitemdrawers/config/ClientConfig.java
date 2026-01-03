package dev.emythiel.createitemdrawers.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    // No group
    public final ConfigBool goggleTooltip = b(false, "goggleTooltip", Comments.goggleTooltip);
    public final ConfigBool goggleTooltipRequiresWrench = b(false, "goggleTooltipRequiresWrench",
        Comments.goggleTooltipRequiresWrench);

    // Rendering group
    public final ConfigGroup renderSettings = group(1, "renderSettings", Comments.renderSettings);
    public final ConfigBool renderItems = b(true, "renderItems", Comments.renderItems);
    public final ConfigInt renderItemsDistance = i(24, 1, 128, "renderItemsDistance",
        Comments.renderItemsDistance);
    public final ConfigBool renderCounts = b(true, "renderCounts", Comments.renderCounts);
    public final ConfigInt renderCountsDistance = i(16, 1, 128, "renderCountsDistance",
        Comments.renderCountsDistance);
    public final ConfigBool renderIcons = b(true, "renderIcons", Comments.renderIcons);
    public final ConfigInt renderIconsDistance = i(6, 1, 128, "renderIconsDistance",
        Comments.renderIconsDistance);

    @Override
    @NotNull
    public String getName() {
        return "client";
    }

    // Comments
    private static class Comments {
        static String goggleTooltip = "Show goggle overlay while looking at a drawer block.";
        static String goggleTooltipRequiresWrench = "Require holding a wrench to be able to see the goggle overlay for a more minimal HUD while using drawers.";

        static String renderSettings = "Settings for adjusting the rendering of items, counts, and more.";
        static String renderItems = "Global setting to enable/disable the rendering of items for all drawers.";
        static String renderItemsDistance = "Sets the distance at which items on the drawers are rendered.";
        static String renderCounts = "Global setting to enable/disable the rendering of item counts for all drawers.";
        static String renderCountsDistance = "Sets the distance at which item counts on the drawers are rendered.";
        static String renderIcons = "Global setting to enable/disable the rendering of lock, void, and upgrade icons";
        static String renderIconsDistance = "Sets the distance at which the icons for locked mode, voiding mode, and upgrades are rendered.";
    }
}
