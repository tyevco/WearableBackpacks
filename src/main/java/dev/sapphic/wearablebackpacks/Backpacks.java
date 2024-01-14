package dev.sapphic.wearablebackpacks;

import com.google.common.collect.ImmutableSet;
import dev.sapphic.wearablebackpacks.block.BackpackBlock;
import dev.sapphic.wearablebackpacks.block.entity.BackpackBlockEntity;
import dev.sapphic.wearablebackpacks.event.BackpackEntityEvents;
import dev.sapphic.wearablebackpacks.inventory.BackpackMenu;
import dev.sapphic.wearablebackpacks.item.BackpackItem;
import dev.sapphic.wearablebackpacks.recipe.BackpackDyeingRecipe;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.logging.Logger;

public final class Backpacks implements ModInitializer {
  public static final String ID = "wearablebackpacks";
  public static final Logger LOGGER = Logger.getLogger(ID);
  public static final Block BLOCK = new BackpackBlock(
                                        FabricBlockSettings
                                                .copyOf(Blocks.WHITE_WOOL)
                                                .mapColor(MapColor.CLEAR)
                                                .strength(0.5F, 0.5F)
                                                .sounds(BlockSoundGroup.WOOL));
  public static Item backpackItem;
  public static BackpackOptions config;

  @Override
  public void onInitialize() {
    AutoConfig.register(BackpackOptions.class, Toml4jConfigSerializer::new);
    config = AutoConfig.getConfigHolder(BackpackOptions.class).getConfig();
    final Identifier backpack = new Identifier(ID, "backpack");
    Registry.register(Registries.BLOCK, backpack, BLOCK);
    Registry.register(Registries.BLOCK_ENTITY_TYPE, backpack, BLOCK_ENTITY);
    backpackItem = new BackpackItem(BLOCK, new Item.Settings());

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
      content.add(backpackItem);
    });
    Registry.register(Registries.ITEM, backpack, backpackItem);
    Item.BLOCK_ITEMS.put(BLOCK, backpackItem);
    Registry.register(Registries.SCREEN_HANDLER, backpack, BackpackMenu.TYPE);
    Registry.register(Registries.RECIPE_SERIALIZER, BackpackDyeingRecipe.ID, BackpackDyeingRecipe.SERIALIZER);
    UseBlockCallback.EVENT.register(BackpackEntityEvents::tryPlaceBackpack);
    LOGGER.info("Wearable Backpacks initialized!");
  }

  public static final BlockEntityType<BackpackBlockEntity> BLOCK_ENTITY = new BlockEntityType<>(BackpackBlockEntity::new, ImmutableSet.of(BLOCK), null);
  
  
}
