package io.github.jason13official.specter.mixin;

import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerTickMixin {

  @Inject(at = @At("TAIL"), method = "tick")
  private void specter$tick(CallbackInfo ci) {

    Player self = (Player) (Object) this;

    if (self instanceof ServerPlayer player && player.tickCount % 20 == 0) {
      SpecterEvents.recallSpecterIfLost(player);
    }
  }
}
