package io.github.jason13official.specter.impl.common.config;

import io.github.jason13official.monolib.api.common.config.ConfigGetterSetter.Commented;

public class ServerModConfig {

  private static float spectersForZombiesChance = 0.05f;
  public static Commented<Float> SPECTERS_FOR_ZOMBIES_CHANCE = new Commented<>(
      "specters_for_zombies_chance",
      () -> spectersForZombiesChance, value -> spectersForZombiesChance = value,
      "Chance that zombies (and husked/drowned) will spawn with a Specter.");
}
