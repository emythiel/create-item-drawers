package dev.emythiel.createitemdrawers.block;

import dev.emythiel.createitemdrawers.block.base.BaseBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class DrawerBlock extends BaseBlock {
    // Number of slots
    private final int slotCount;

    public DrawerBlock(Properties properties, int slotCount) {
        super(properties);
        if (slotCount < 1) throw new IllegalArgumentException("slotCount must be >= 1");
        this.slotCount = slotCount;
    }

    // Getter for slots the drawer has
    public int getSlotCount() {
        return slotCount;
    }
}
