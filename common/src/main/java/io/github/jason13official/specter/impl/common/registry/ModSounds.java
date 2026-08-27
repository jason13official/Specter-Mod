package io.github.jason13official.specter.impl.common.registry;

import io.github.jason13official.specter.SpecterMod;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

  public static SoundEvent SPECTER_SHELL;
  public static SoundEvent SPECTER_BEAM;

  public static void register(BiConsumer<SoundEvent, ResourceLocation> consumer) {

    SPECTER_SHELL = SoundEvent.createVariableRangeEvent(SpecterMod.identifier("specter_shell"));
    consumer.accept(SPECTER_SHELL, SpecterMod.identifier("specter_shell"));

    SPECTER_BEAM = SoundEvent.createVariableRangeEvent(SpecterMod.identifier("specter_beam"));
    consumer.accept(SPECTER_BEAM, SpecterMod.identifier("specter_beam"));
  }
}
