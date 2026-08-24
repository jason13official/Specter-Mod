package io.github.jason13official.specter.impl.common.registry;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.platform.Services;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTabs {

  public static CreativeModeTab SPECTER;

  public static void register(BiConsumer<CreativeModeTab, ResourceLocation> consumer) {

    SPECTER = Services.PLATFORM.tabBuilder()
        .icon(() -> new ItemStack(ModItems.CONDENSED_SPECTER))
        .title(Component.translatable("itemGroup.specter"))
        .displayItems((itemDisplayParameters, output) -> {

          output.accept(ModItems.SPECTER_CORE);
          output.accept(ModItems.CONDENSED_SPECTER);
        })
        .build();

    consumer.accept(SPECTER, SpecterMod.identifier("specter"));
  }
}
