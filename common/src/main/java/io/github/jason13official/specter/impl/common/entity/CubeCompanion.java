package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CubeCompanion extends AbstractSpecter{

  public CubeCompanion(EntityType<? extends AbstractSpecter> entityType, Level level) {
    super(entityType, level);
  }

  public CubeCompanion(Level level, @Nullable LivingEntity owner) {
    this(ModEntities.CUBE, level);
    this.setOwner(owner);
  }
}
