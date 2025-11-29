package dev.emythiel.createitemdrawers.block;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.IBE;
import dev.emythiel.createitemdrawers.block.base.BaseBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import net.minecraft.core.BlockPos;
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

    public DrawerBlock(Properties properties, int slotCount) {
        super(properties);
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
    public int getSlotCount() { return slotCount; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrawerBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!isFrontFace(state, hit.getDirection()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        // Open GUI if front face is right-clicked with Create Wrench
        if (held.is(AllItems.WRENCH.get())) {
            if (!level.isClientSide) {
                player.openMenu(be, buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }

        int slot = DrawerInteractionHelper.getHitSlot(be, hit.getLocation());
        if (slot < 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean sneaking = player.isShiftKeyDown();
        DrawerSlot drawerSlot = be.getStorage().getSlot(slot);


        // Attempt to insert held item first
        if (!held.isEmpty()) {
            int before = held.getCount();
            ItemStack leftover = be.getStorage().insert(slot, held, false);
            boolean inserted = leftover.getCount() < before;

            player.setItemInHand(hand, leftover);

            if (inserted) {
                be.setChangedAndSync();

                if (!sneaking)
                    return ItemInteractionResult.SUCCESS;
            }
        }

        ItemStack stored = drawerSlot.getStoredItem();

        if (sneaking) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (inv.isEmpty())
                    continue;

                if (!ItemStack.isSameItemSameComponents(inv, stored))
                    continue;

                ItemStack leftover = be.getStorage().insert(slot, inv, false);
                player.getInventory().setItem(i, leftover);
            }
            be.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.SUCCESS;
    }
}
