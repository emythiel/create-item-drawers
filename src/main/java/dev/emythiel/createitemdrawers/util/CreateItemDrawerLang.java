package dev.emythiel.createitemdrawers.util;

import com.simibubi.create.foundation.utility.CreateLang;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.createmod.catnip.lang.LangBuilder;

public class CreateItemDrawerLang extends CreateLang {

    public static LangBuilder builder() {
        return new LangBuilder(CreateItemDrawers.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder translateEmptyLine(Object... args) {
        return new LangBuilder("");
    }
}
