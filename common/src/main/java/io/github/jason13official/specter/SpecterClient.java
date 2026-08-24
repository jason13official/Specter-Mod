package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.common.item.DyeableCondensedSpecterItem;
import java.util.function.BiFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class SpecterClient {

  /// tintIndex 0 = the dyeable layer; anything else renders undyed
  public static final BiFunction<ItemStack, Integer, Integer> DYED_ITEM_COLOR_FN =
      (stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyeableCondensedSpecterItem.DEFAULT_SPECTER_COLOR);

  public static void init() {
  }
}
