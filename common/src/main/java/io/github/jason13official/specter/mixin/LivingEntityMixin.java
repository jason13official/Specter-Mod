package io.github.jason13official.specter.mixin;

import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Ghosts fully resurrect their Guardian in the lore; closest gameplay equivalent is a totem
/// of undying, gated on having your own Specter nearby to do the reviving
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

  @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
  private void specter$checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {

    LivingEntity self = (LivingEntity) (Object) this;

    if (self instanceof Player player && self.level() instanceof ServerLevel level && AbstractSpecter.findOwned(level, player) != null) {
      self.setHealth(2.0f);
      cir.setReturnValue(true);
    }
  }
}
