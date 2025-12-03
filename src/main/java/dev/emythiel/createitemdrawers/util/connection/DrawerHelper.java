package dev.emythiel.createitemdrawers.util.connection;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler.ConnectedGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DrawerHelper {

    public static DrawerBlockEntity getDrawer(BlockAndTintGetter reader, BlockPos pos) {
        BlockEntity be = reader.getBlockEntity(pos);
        if (!(be instanceof DrawerBlockEntity))
            return null;
        return (DrawerBlockEntity) be;
    }

    public static ConnectedGroup getInput(BlockAndTintGetter reader, BlockPos pos) {
        DrawerBlockEntity drawer = getDrawer(reader, pos);
        return drawer == null ? null : drawer.group;
    }

    public static boolean areDrawersConnected(BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos) {
        ConnectedGroup input1 = getInput(reader, pos);
        ConnectedGroup input2 = getInput(reader, otherPos);

        if (input1 == null || input2 == null)
            return false;
        if (input1.offsets.isEmpty() || input2.offsets.isEmpty())
            return false;
        try {
            if (pos.offset(input1.offsets.get(0)).equals(otherPos.offset(input2.offsets.get(0))))
                return true;
        } catch (IndexOutOfBoundsException e) {
            // race condition
        }

        return false;
    }
}
