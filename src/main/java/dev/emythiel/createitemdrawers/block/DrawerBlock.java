package dev.emythiel.createitemdrawers.block;

import com.simibubi.create.foundation.block.IBE;
import dev.emythiel.createitemdrawers.block.base.BaseBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DrawerBlock extends BaseBlock implements IBE<DrawerBlockEntity> {
    // Number of slots
    private final int slotCount;

    public DrawerBlock(Properties properties, int slotCount) {
        super(properties);
        if (slotCount < 1) throw new IllegalArgumentException("slotCount must be >= 1");
        this.slotCount = slotCount;
    }

    @Override
    public Class<DrawerBlockEntity> getBlockEntityClass() {
        return DrawerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DrawerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.DRAWER_BLOCK_ENTITY.get();
    }

    // Getter for slots the drawer has
    public int getSlotCount() {
        return slotCount;
    }
}
