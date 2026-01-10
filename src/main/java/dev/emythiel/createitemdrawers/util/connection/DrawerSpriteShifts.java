package dev.emythiel.createitemdrawers.util.connection;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import dev.emythiel.createitemdrawers.CreateItemDrawers;

public class DrawerSpriteShifts {

    public static final CTSpriteShiftEntry
        DRAWER_BACK = omni("back"),
        DRAWER_SIDE_VER = vertical("side"),
        DRAWER_SIDE_HOR = horizontal("side"),
        DRAWER_TRIM_VER = vertical("trim"),
        DRAWER_TRIM_HOR = horizontal("trim");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreateItemDrawers.asResource("block/" + blockTextureName),
            CreateItemDrawers.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static void init() {}
}
