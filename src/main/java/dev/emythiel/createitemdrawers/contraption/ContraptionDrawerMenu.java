package dev.emythiel.createitemdrawers.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.emythiel.createitemdrawers.gui.AbstractDrawerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ContraptionDrawerMenu extends AbstractDrawerMenu<DrawerMountedStorage> {

    public int contraptionEntityId;
    public BlockPos localPos;

    public ContraptionDrawerMenu(MenuType<?> type, int id, Inventory inv,
                                 DrawerMountedStorage storage, int entityId, BlockPos localPos) {
        super(type, id, inv, storage);
        this.contraptionEntityId = entityId;
        this.localPos = localPos;
    }

    public ContraptionDrawerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        super(type, id, inv, buf);
    }

    @Override
    protected DrawerMountedStorage createOnClient(RegistryFriendlyByteBuf buf) {
        this.contraptionEntityId = buf.readInt();
        this.localPos = buf.readBlockPos();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;

        Entity entity = level.getEntity(contraptionEntityId);
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) return null;

        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || info.nbt() == null) return null;

        return DrawerMountedStorage.fromBlockInfoNbt(info.nbt(), level.registryAccess());
    }

}
