package io.github.jason13official.specter.api.common.util;

import net.minecraft.nbt.CompoundTag;

public interface SpecterDataHolder {

  CompoundTag specter$getPersistentData();

  void specter$setPersistentData(CompoundTag compoundTag);
}
