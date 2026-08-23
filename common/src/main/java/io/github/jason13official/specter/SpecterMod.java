package io.github.jason13official.specter;

import net.minecraft.resources.ResourceLocation;

public class SpecterMod {

  public static void init() {
  }

  public static ResourceLocation identifier(final String path) {
    return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
  }
}