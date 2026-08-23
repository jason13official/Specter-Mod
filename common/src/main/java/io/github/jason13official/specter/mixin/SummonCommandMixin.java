package io.github.jason13official.specter.mixin;

import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public class SummonCommandMixin {

  @Inject(at = @At("RETURN"), method = "createEntity")
  private static void specter$createEntity(CommandSourceStack source, Reference<EntityType<?>> type, Vec3 pos, CompoundTag tag, boolean randomizeProperties, CallbackInfoReturnable<Entity> cir) {

    if (cir.getReturnValue() != null) {
      Entity entity = cir.getReturnValue();
      if (entity instanceof AbstractSpecter specter) {
        specter.setOwner(source.getPlayer());
      }
    }
  }
}
