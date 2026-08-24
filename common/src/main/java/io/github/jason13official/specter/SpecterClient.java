package io.github.jason13official.specter;

import java.util.function.BiFunction;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class SpecterClient {

  /// tintIndex 0 = the dyeable layer; anything else renders undyed
  public static final BiFunction<ItemStack, Integer, Integer> DYED_ITEM_COLOR_FN =
      (stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack);

  public static void init() {
  }
}
