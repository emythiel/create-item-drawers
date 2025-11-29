package dev.emythiel.createitemdrawers.registry;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> DRAWERS = blockTag("drawers");

        private static TagKey<Block> blockTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> UPGRADES = itemTag("upgrades");
        public static final TagKey<Item> DRAWERS = itemTag("drawers");

        private static TagKey<Item> itemTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateItemDrawers.MODID, name));
        }
    }
}
