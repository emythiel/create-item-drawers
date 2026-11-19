package dev.emythiel.createitemdrawers.util.connection;

import dev.emythiel.createitemdrawers.block.base.BaseDrawerBlockEntity;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler.ConnectedGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConnectionHelper {

    public static BaseDrawerBlockEntity getDrawer(BlockAndTintGetter reader, BlockPos pos) {
        BlockEntity be = reader.getBlockEntity(pos);
        if (!(be instanceof BaseDrawerBlockEntity))
            return null;
        return (BaseDrawerBlockEntity) be;
    }

    public static ConnectedGroup getInput(BlockAndTintGetter reader, BlockPos pos) {
        BaseDrawerBlockEntity drawer = getDrawer(reader, pos);
        return drawer == null ? null : drawer.group;
    }

    public static boolean areDrawersConnected(BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos) {
        ConnectedGroup input1 = getInput(reader, pos);
        ConnectedGroup input2 = getInput(reader, otherPos);

        if (input1 == null || input2 == null)
            return false;

        // Two drawers share a network exactly when they resolve to the same controller.
        // Resolving the controller handles empty or partially written offset lists
        // without relying on an exception for control flow.
        return input1.getController(pos).equals(input2.getController(otherPos));
    }
}
