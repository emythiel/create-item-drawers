package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.emythiel.createitemdrawers.CreateItemDrawers;

public class CreateItemDrawersRegistrate {
    public static final CreateRegistrate REGISTRATE =
        CreateRegistrate.create(CreateItemDrawers.MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> TooltipModifier.EMPTY);
        REGISTRATE.defaultCreativeTab(ModCreativeModeTab.ITEM_DRAWERS_TAB_KEY);
    }
}
