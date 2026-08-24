package io.github.jason13official.specter.impl.common.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier extends LootModifier {

  public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item)).apply(inst, AddItemModifier::new)));

  private final Item item;

  protected AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
    super(conditionsIn);
    this.item = item;
  }

  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

    if (context.getRandom().nextFloat() >= 0.01f) {
      generatedLoot.add(new ItemStack(item));
    }

    return generatedLoot;
  }


  @Override
  public MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
