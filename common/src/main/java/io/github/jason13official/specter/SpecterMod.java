package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.common.util.ModConfigIO;
import net.minecraft.resources.ResourceLocation;


public class SpecterMod {

  public static void init() {

    ModConfigIO.loadOrInitialize();
  }

  public static ResourceLocation identifier(final String path) {
    return new ResourceLocation(Constants.MOD_ID, path);
  }
}