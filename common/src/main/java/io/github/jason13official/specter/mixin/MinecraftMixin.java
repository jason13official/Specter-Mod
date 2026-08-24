package io.github.jason13official.specter.mixin;

import io.github.jason13official.specter.Constants;
import io.github.jason13official.specter.platform.Services;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Inject(at = @At("TAIL"), method = "<init>")
  private void init(CallbackInfo info) {

    if (Services.PLATFORM.isDevelopmentEnvironment()) {
      Constants.LOG.info("This line is printed by an example mixin from Common!");
    }
  }
}