package dev.emythiel.createitemdrawers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.emythiel.createitemdrawers.gui.DrawerScreen;
import dev.emythiel.createitemdrawers.gui.DrawerMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModMenus {

    public static final MenuEntry<DrawerMenu> DRAWER_MENU =
        register("drawer_gui", DrawerMenu::new, () -> DrawerScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
        String name, ForgeMenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
        return CreateItemDrawersRegistrate.REGISTRATE
            .menu(name, factory, screenFactory)
            .register();
    }

    public static void register() {}
}
