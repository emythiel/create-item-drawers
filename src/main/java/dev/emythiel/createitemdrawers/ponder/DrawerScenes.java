package dev.emythiel.createitemdrawers.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DrawerScenes {

    public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("drawer_intro", "Item Drawers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.idle(15);

        BlockPos doubleDrawer = util.grid().at(2,1,2);
        scene.overlay().showText(80)
            .text("Item Drawers can be used to store a large amount of a few items")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().blockSurface(doubleDrawer, Direction.NORTH));
        scene.idle(90);

        scene.overlay().showText(100)
            .text("Depending on the drawer type, you can store up to 1, 2, or 4 different items.")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().blockSurface(doubleDrawer, Direction.UP));
        scene.idle(20);
        scene.overlay().showFilterSlotInput(new Vec3(3.5,1.5,2), Direction.NORTH, 75); // single
        scene.idle(15);
        scene.overlay().showFilterSlotInput(new Vec3(2.5,1.75,2), Direction.NORTH, 60); // double top
        scene.idle(5);
        scene.overlay().showFilterSlotInput(new Vec3(2.5,1.25,2), Direction.NORTH, 55); // double bottom
        scene.idle(15);
        scene.overlay().showFilterSlotInput(new Vec3(1.75,1.75,2), Direction.NORTH, 40); // quad top-left
        scene.idle(5);
        scene.overlay().showFilterSlotInput(new Vec3(1.25,1.75,2), Direction.NORTH, 35); // quad top-right
        scene.idle(5);
        scene.overlay().showFilterSlotInput(new Vec3(1.75,1.25,2), Direction.NORTH, 30); // quad bottom-left
        scene.idle(5);
        scene.overlay().showFilterSlotInput(new Vec3(1.25,1.25,2), Direction.NORTH, 25); // quad bottom-right
        scene.idle(70);

        scene.overlay().showText(80)
            .text("Right-click a drawer slot to insert your held item(s)")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().topOf(doubleDrawer));
        scene.idle(30);
        scene.overlay().showControls(new Vec3(2.5,1.60,2), Pointing.UP, 25)
            .rightClick();
        scene.idle(5);
        scene.world().modifyBlockEntity(doubleDrawer, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.OAK_PLANKS).copyWithCount(64), false);

        });
        scene.idle(35);
        scene.overlay().showControls(new Vec3(2.5,1.10,2), Pointing.UP, 25)
            .rightClick();
        scene.idle(5);
        scene.world().modifyBlockEntity(doubleDrawer, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(1, new ItemStack(Items.IRON_INGOT).copyWithCount(36), false);
        });
        scene.idle(40);
        scene.overlay().showText(80)
            .text("Or Sneak+Right-click to insert matching items from your inventory")
            .placeNearTarget()
            .pointAt(util.vector().topOf(doubleDrawer));
        scene.idle(30);
        scene.overlay().showControls(new Vec3(3.5,1.10,2), Pointing.UP, 25)
            .rightClick()
            .whileSneaking();
        scene.idle(5);
        BlockPos singleDrawer = util.grid().at(3,1,2);
        scene.world().modifyBlockEntity(singleDrawer, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.GRASS_BLOCK).copyWithCount(420), false);
        });
        scene.idle(60);

        scene.overlay().showText(80)
            .text("Left-click a slot to take 1 item out")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().topOf(doubleDrawer));
        scene.idle(30);
        scene.overlay().showControls(new Vec3(3.5, 1.10,2), Pointing.UP, 25)
            .rightClick();
        scene.idle(5);
        scene.world().modifyBlockEntity(singleDrawer, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(419);
        });
        scene.idle(60);
        scene.overlay().showText(80)
            .text("Or Sneak+Left-click to take out a stack")
            .placeNearTarget()
            .pointAt(util.vector().topOf(doubleDrawer));
        scene.idle(30);
        scene.overlay().showControls(new Vec3(3.5, 1.10, 2), Pointing.UP, 25)
            .rightClick()
            .whileSneaking();
        scene.idle(5);
        scene.world().modifyBlockEntity(singleDrawer, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(355);
        });
        scene.idle(60);

        scene.overlay().showText(100)
            .text("Right-click the front of a drawer with a wrench to open the settings menu")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().topOf(doubleDrawer));
        scene.idle(15);
        scene.overlay().showControls(new Vec3(2.5, 1.5, 2), Pointing.UP, 50)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(60);

        scene.markAsFinished();
    }

    public static void connecting(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("drawer_connecting", "Connecting Item Drawers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        Selection drawers = util.select().fromTo(2,1,2,3,3,2);
        Selection largeCog = util.select().position(2,0,5);
        Selection shaft1 = util.select().fromTo(1,1,3,1,1,5);
        Selection belt1 = util.select().fromTo(0,1,2,1,2,2);
        Selection shaft2 = util.select().fromTo(2,1,3,3,1,3);
        Selection belt2 = util.select().fromTo(4,1,0,4,2,3);

        scene.world().showSection(drawers, Direction.NORTH);
        scene.idle(10);
        scene.overlay().showText(70)
            .text("Drawers can be connected to each other to combine their inventories")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(new Vec3(3,2.5,2));
        scene.idle(80);

        scene.rotateCameraY(-180);
        scene.idle(40);

        Vec3 v = util.vector().blockSurface(util.grid().at(3,2,2), Direction.WEST);
        AABB bb = new AABB(v, v).inflate(.125f, .5, .5);
        v = v.add(0,0,.5);

        scene.addKeyframe();
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.WHITE, new Object(), bb, 45);
        scene.overlay().showControls(v, Pointing.LEFT, 40)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(7);
        DrawerPonderInstructions.connectDrawers(scene, util.grid().at(2,2,2), util.grid().at(3,2,2));
        scene.idle(40);
        scene.overlay().showOutlineWithText(util.select().fromTo(2,2,2,3,2,2), 70)
            .text("Right-click drawers at their backs or sides to connect them to each other")
            .placeNearTarget()
            .pointAt(v);
        scene.idle(80);
        scene.overlay().showControls(v.add(0,1,0), Pointing.LEFT, 20)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(7);
        DrawerPonderInstructions.connectDrawers(scene, util.grid().at(2,3,2), util.grid().at(3,3,2));
        scene.idle(20);
        scene.overlay().showControls(v.add(0,-1,0), Pointing.LEFT, 20)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(7);
        DrawerPonderInstructions.connectDrawers(scene, util.grid().at(2,1,2), util.grid().at(3,1,2));
        scene.idle(20);
        scene.overlay().showControls(v.add(.5,-.5,0), Pointing.LEFT, 20)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(7);
        DrawerPonderInstructions.connectDrawers(scene, util.grid().at(2,1,2), util.grid().at(2,2,2));
        scene.idle(20);
        scene.overlay().showControls(v.add(.5,.5,0), Pointing.LEFT, 20)
            .withItem(AllItems.WRENCH.asStack())
            .rightClick();
        scene.idle(7);
        DrawerPonderInstructions.connectDrawers(scene, util.grid().at(2,2,2), util.grid().at(2,3,2));
        scene.idle(30);

        scene.rotateCameraY(180);
        scene.idle(15);
        scene.addKeyframe();
        scene.idle(15);

        scene.world().showSection(largeCog, Direction.NORTH);
        scene.world().showSection(shaft1, Direction.NORTH);
        scene.world().showSection(belt1, Direction.EAST);

        BlockPos belt1Start = util.grid().at(0,1,2);
        BlockPos belt1Stop = util.grid().at(1,1,2);
        BlockPos funnelIn = util.grid().at(1,2,2);
        BlockPos drawerMidRightS = util.grid().at(2,2,2);
        BlockPos drawerMidLeftD = util.grid().at(3,2,2);
        BlockPos drawerBotRightS = util.grid().at(2,1,2);

        scene.idle(25);
        scene.world().createItemOnBelt(belt1Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS).copyWithCount(60));
        scene.idle(22);
        scene.world().flapFunnel(funnelIn, false);
        scene.world().removeItemsFromBelt(belt1Stop);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.OAK_PLANKS).copyWithCount(60), false);
        });
        scene.overlay().showOutlineWithText(util.select().fromTo(2,1,2,3,3,2), 70)
            .text("Any components for item transfer can now both insert...")
            .colored(PonderPalette.GREEN)
            .placeNearTarget()
            .pointAt(v);
        scene.world().createItemOnBelt(belt1Start, Direction.WEST, new ItemStack(Items.IRON_INGOT).copyWithCount(46));
        scene.idle(22);
        scene.world().flapFunnel(funnelIn, false);
        scene.world().removeItemsFromBelt(belt1Stop);
        scene.world().modifyBlockEntity(drawerMidLeftD, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.IRON_INGOT).copyWithCount(46), false);
        });
        scene.world().createItemOnBelt(belt1Start, Direction.WEST, new ItemStack(Items.GOLD_INGOT).copyWithCount(31));
        scene.idle(22);
        scene.world().flapFunnel(funnelIn, false);
        scene.world().removeItemsFromBelt(belt1Stop);
        scene.world().modifyBlockEntity(drawerMidLeftD, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(1, new ItemStack(Items.GOLD_INGOT).copyWithCount(31), false);
        });
        scene.world().createItemOnBelt(belt1Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS).copyWithCount(44));
        scene.idle(22);
        scene.world().flapFunnel(funnelIn, false);
        scene.world().removeItemsFromBelt(belt1Stop);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.OAK_PLANKS).copyWithCount(44), false);
        });
        scene.world().createItemOnBelt(belt1Start, Direction.WEST, new ItemStack(Items.DIAMOND).copyWithCount(12));
        scene.idle(22);
        scene.world().flapFunnel(funnelIn, false);
        scene.world().removeItemsFromBelt(belt1Stop);
        scene.world().modifyBlockEntity(drawerBotRightS, DrawerBlockEntity.class, (be) -> {
            be.getLocalHandler().insertItem(0, new ItemStack(Items.DIAMOND).copyWithCount(12), false);
        });
        scene.idle(10);

        scene.rotateCameraY(90);
        scene.world().showSection(shaft2, Direction.WEST);
        scene.world().showSection(belt2, Direction.WEST);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().fromTo(2,1,2,3,3,2), 70)
            .text("...and take contents from the connected drawers")
            .colored(PonderPalette.GREEN)
            .placeNearTarget()
            .pointAt(new Vec3(2.5,3,2));

        BlockPos belt2Start = util.grid().at(4,1,2);
        BlockPos funnelOut = util.grid().at(4,2,2);

        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(103);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(102);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(101);
        });
        scene.markAsFinished();
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(100);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(99);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(98);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
        scene.world().modifyBlockEntity(drawerMidRightS, DrawerBlockEntity.class, (be) -> {
            be.getStorage().getSlot(0).setCount(97);
        });
        scene.world().flapFunnel(funnelOut, true);
        scene.world().createItemOnBelt(belt2Start, Direction.WEST, new ItemStack(Items.OAK_PLANKS));
        scene.idle(22);
    }
}
