package io.github.jason13official.specter.impl.common.loot;

import io.github.jason13official.specter.impl.common.registry.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class SpecterLootModifiersFabric {

  private static ResourceKey<LootTable> DESERT_PYRAMID;
  private static ResourceKey<LootTable> JUNGLE_TEMPLE;

  public static void register() {

    DESERT_PYRAMID = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/desert_pyramid"));
    JUNGLE_TEMPLE = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/jungle_temple"));

    LootTableEvents.MODIFY.register((key, builder, lootTableSource, provider) -> {


      if (key.equals(DESERT_PYRAMID) || key.equals(JUNGLE_TEMPLE)) {

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
