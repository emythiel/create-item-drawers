package dev.emythiel.createitemdrawers.block;

import com.simibubi.create.foundation.block.IBE;
import dev.emythiel.createitemdrawers.block.base.BaseBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DrawerBlock extends BaseBlock implements IBE<DrawerBlockEntity> {

    private final int slotCount;
    private final int baseMultiplier;

    public DrawerBlock(Properties properties, int slotCount, int baseMultiplier) {
        super(properties);
        this.slotCount = slotCount;
        this.baseMultiplier = baseMultiplier;
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
    public int getSlotCount() { return slotCount; }
    public int getBaseMultiplier() { return baseMultiplier; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrawerBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!isFrontFace(state, hit.getDirection()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        int slot = DrawerInteractionHelper.getHitSlot(be, hit.getLocation());
        if (slot < 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!held.isEmpty()) {
            ItemStack leftover = be.getStorage().insert(slot, held, false);
            player.setItemInHand(hand, leftover);
            be.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
