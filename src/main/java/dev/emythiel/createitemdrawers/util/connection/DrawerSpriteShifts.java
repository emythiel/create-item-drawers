package dev.emythiel.createitemdrawers.util.connection;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import dev.emythiel.createitemdrawers.CreateItemDrawers;

public class DrawerSpriteShifts {

    public static final CTSpriteShiftEntry
        DRAWER_BACK = getCT(AllCTTypes.OMNIDIRECTIONAL, "back"),
        DRAWER_SIDE_HOR = getCT(AllCTTypes.HORIZONTAL, "side"),
        DRAWER_SIDE_VER = getCT(AllCTTypes.VERTICAL, "side"),
        DRAWER_TRIM = getCT(AllCTTypes.OMNIDIRECTIONAL, "trim");

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreateItemDrawers.asResource("block/" + blockTextureName),
            CreateItemDrawers.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static void init() {}
}
