package dev.emythiel.createitemdrawers.ponder;

import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.minecraft.core.BlockPos;

public class DrawerPonderInstructions  {

    public static void connectDrawers(SceneBuilder scene, BlockPos pos1, BlockPos pos2) {
        scene.addInstruction(s -> {
            ConnectedGroupHandler.toggleConnection(s.getWorld(), pos1, pos2);
            s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
        });
    }
}
