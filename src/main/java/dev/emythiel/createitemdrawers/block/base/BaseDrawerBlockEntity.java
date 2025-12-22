package dev.emythiel.createitemdrawers.block.base;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public abstract class BaseDrawerBlockEntity extends SmartBlockEntity implements TransformableBlockEntity {

    public ConnectedGroupHandler.ConnectedGroup group = new ConnectedGroupHandler.ConnectedGroup();
    private IItemHandler combinedHandler;
    protected boolean reRender;

    public BaseDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract IItemHandler getLocalHandler();
    public abstract BaseDrawerBlockEntity getController();

    public ConnectedGroupHandler.ConnectedGroup getGroup() {
        return group;
    }

    public IItemHandler getItemHandler(Direction side) {
        if (combinedHandler == null) {
            combinedHandler = ConnectedGroupHandler.buildCombinedHandler(this);
            if (combinedHandler == null) {
                combinedHandler = getLocalHandler();
            }
        }
        return combinedHandler;
    }

    public void connectivityChanged() {
        reRender = true;
        sendData();
        combinedHandler = null;
        invalidateCapabilities();
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        group.offsets.replaceAll(transform::applyWithoutOffset);
        notifyUpdate();
    }

    public void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        EdgeInteractionBehaviour connectivity = new EdgeInteractionBehaviour(this, ConnectedGroupHandler::toggleConnection)
            .connectivity(ConnectedGroupHandler::shouldConnect)
            .require(AllItems.WRENCH.get());
        behaviours.add(connectivity);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeSafe(tag, provider);
        if (group == null) return;

        CompoundTag groupTag = new CompoundTag();
        group.write(groupTag);
        tag.put("ConnectedGroup", groupTag);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
        super.write(tag, provider, clientPacket);

        CompoundTag groupTag = new CompoundTag();
        group.write(groupTag);
        tag.put("ConnectedGroup", groupTag);

        if (clientPacket && reRender) {
            tag.putBoolean("Redraw", true);
            reRender = false;
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
        super.read(tag, provider, clientPacket);

        group.read(tag.getCompound("ConnectedGroup"));

        if (!clientPacket) return;
        if (tag.contains("Redraw")) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event,
                                            BlockEntityType<? extends BaseDrawerBlockEntity> type) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            type,
            BaseDrawerBlockEntity::getItemHandler
        );
    }
}
