package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Specter extends AbstractSpecter {

  public Specter(EntityType<? extends AbstractSpecter> entityType, Level level) {
    super(entityType, level);
  }

  public Specter(Level level, @Nullable LivingEntity owner) {
    this(ModEntities.SPECTER, level);
    this.setOwner(owner);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.tickCount % 20 == 0) {
      healOwner();
    }
  }

  /// lore: Ghosts channel the Traveler's Light to mend flesh and cure poisons;
  /// only while close enough to be "shielding" the owner
  private void healOwner() {

    if (!(this.level() instanceof ServerLevel)) return;
    if (!(this.getOwner() instanceof LivingEntity owner)) return;
    if (owner.isDeadOrDying()) return;
    if (this.distanceTo(owner) >= 4.0f) return;

    boolean healed = false;

    if (owner.getHealth() < owner.getMaxHealth() * 0.5f) {
      owner.heal(2.0f);
      healed = true;
    }

    if (owner.removeEffect(MobEffects.POISON)) {
      healed = true;
    }

    if (!healed) return;

    if (!owner.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
      owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 5, 0, true, true));
    }

    owner.level().playSound(null, this.blockPosition(), SoundEvents.ALLAY_THROW, SoundSource.AMBIENT, 0.6f, 0.8f);

//    Vec3 pos = this.position();
//    for (int i = 0; i < 4; i++) {
//      float offset = (owner.getRandom().nextFloat() * 2) - 1;
//      owner.level().addParticle(ParticleTypes.HAPPY_VILLAGER, pos.x + (offset / 2), pos.y + (offset / 2), pos.z + (offset / 2), offset, offset, offset);
//    }
  }
}
