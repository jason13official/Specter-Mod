package io.github.jason13official.specter.impl.common.registry;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.menu.SpecterMenu;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

  public static MenuType<SpecterMenu> SPECTER;

  public static void register(BiConsumer<MenuType<?>, ResourceLocation> consumer) {

    SPECTER = new MenuType<>(SpecterMenu::new, FeatureFlags.VANILLA_SET);

    consumer.accept(SPECTER, SpecterMod.identifier("specter"));
  }
}
