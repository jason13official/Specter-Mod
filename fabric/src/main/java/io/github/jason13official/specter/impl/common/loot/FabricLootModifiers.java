package io.github.jason13official.specter.impl.common.loot;

import io.github.jason13official.specter.impl.common.registry.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class FabricLootModifiers {

  public static final ResourceLocation DESERT_PYRAMID = new ResourceLocation("chests/desert_pyramid");
  public static final ResourceLocation JUNGLE_TEMPLE = new ResourceLocation("chests/jungle_temple");

  public static void register() {
    LootTableEvents.MODIFY.register((resourceManager, lootDataManager, resourceLocation, builder, lootTableSource) -> {

      if (resourceLocation.equals(DESERT_PYRAMID) || resourceLocation.equals(JUNGLE_TEMPLE)) {

        LootPool.Builder poolBuilder = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0f))
            .conditionally(LootItemRandomChanceCondition.randomChance(0.01f).build())
            .with(LootItem.lootTableItem(ModItems.SPECTER_CORE).build())
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 1.0f)));

        builder.pool(poolBuilder.build());
      }

    });
  }
}
