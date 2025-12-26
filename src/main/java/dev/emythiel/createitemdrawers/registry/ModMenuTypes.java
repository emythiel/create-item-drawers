package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.gui.DrawerScreen;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModMenuTypes {

    private static final CreateRegistrate REGISTRATE = CreateItemDrawers.registrate();

    public static final MenuEntry<DrawerMenu> DRAWER_MENU =
        register("drawer_menu", DrawerMenu::new, () -> DrawerScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
        String name, ForgeMenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
        return REGISTRATE
            .menu(name, factory, screenFactory)
            .register();
    }

    public static void register() {}
}
