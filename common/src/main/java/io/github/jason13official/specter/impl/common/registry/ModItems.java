package io.github.jason13official.specter.impl.common.registry;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.item.DyeableCondensedSpecterItem;
import io.github.jason13official.specter.impl.common.item.SpecterCoreItem;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {

  public static Item SPECTER_CORE;
  public static Item CONDENSED_SPECTER;

  public static void register(BiConsumer<Item, ResourceLocation> consumer) {

    SPECTER_CORE = new SpecterCoreItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
    CONDENSED_SPECTER = new DyeableCondensedSpecterItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));

    consumer.accept(SPECTER_CORE, SpecterMod.identifier("specter_core"));
    consumer.accept(CONDENSED_SPECTER, SpecterMod.identifier("condensed_specter"));
  }
}
