package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.impl.common.item.DyeableCondensedSpecterItem;
import io.github.jason13official.specter.impl.common.menu.SpecterMenu;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModSounds;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Specter extends AbstractSpecter {

  private static final EntityDataAccessor<Integer> DATA_ATTACK_TARGET = SynchedEntityData.defineId(Specter.class, EntityDataSerializers.INT);

  private static final double ATTACK_RANGE = 16.0;
  private static final int ATTACK_INTERVAL = 15;
  private static final float CRITICAL_HEALTH_FRACTION = 0.2f;

  private int attackCooldown;
  private @Nullable LivingEntity clientSideCachedAttackTarget;

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

  /// enables middle-click / right-click interactions
  @Override
  public boolean isPickable() {

    return !this.isRemoved();
  }

  /// shift + empty main hand converts us into an item; a plain right-click opens the menu
  @Override
  protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {

    ItemStack heldItem = player.getItemInHand(hand);

    // redstone heal: we only naturally heal back to half health, so give owner a way past that
    if (this.getOwner() == player && hand == InteractionHand.MAIN_HAND && this.getHealth() < this.getMaxHealth()) {

      float healAmount = heldItem.is(Items.REDSTONE_BLOCK) ? 9.0f : heldItem.is(Items.REDSTONE) ? 1.0f : 0.0f;

      if (healAmount > 0.0f) {
        if (!this.level().isClientSide) {
          this.heal(healAmount);
          if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
          }
          this.level().playSound(null, this.blockPosition(), ModSounds.SPECTER_SHELL, SoundSource.AMBIENT);
        }

        return InteractionResult.SUCCESS;
      }
    }

    if (this.getOwner() == player && hand == InteractionHand.MAIN_HAND && heldItem.isEmpty()) {

      if (player.isShiftKeyDown()) {
        player.setItemInHand(hand, this.toCondensedItemStack());
        this.discard();
      } else if (!this.level().isClientSide) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new SpecterMenu(id, inv), this.getDisplayName()));
      }
    }

    return super.mobInteract(player, hand);
  }

  private ItemStack toCondensedItemStack() {

    ItemStack stack = new ItemStack(ModItems.CONDENSED_SPECTER);

    if (this.getSpecterColor() != DyeableCondensedSpecterItem.DEFAULT_SPECTER_COLOR) {
      stack.set(DataComponents.DYED_COLOR, new DyedItemColor(this.getSpecterColor(), true));
    }

    if (this.hasCustomName()) {
      stack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
    }

    return stack;
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.Builder builder) {
    super.defineSynchedData(builder);

    builder.define(DATA_ATTACK_TARGET, 0);
  }

  @Override
  public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);

    if (DATA_ATTACK_TARGET.equals(key)) {
      this.clientSideCachedAttackTarget = null;
    }
  }

  @Override
  protected @Nullable LivingEntity getLookFocus() {

    LivingEntity target = this.getTarget();
    return target != null ? target : super.getLookFocus();
  }

  public boolean hasActiveAttackTarget() {

    return this.entityData.get(DATA_ATTACK_TARGET) != 0;
  }

  public @Nullable LivingEntity getActiveAttackTarget() {

    if (!this.hasActiveAttackTarget()) return null;

    if (!this.level().isClientSide) return this.getTarget();

    if (this.clientSideCachedAttackTarget != null) return this.clientSideCachedAttackTarget;

    Entity entity = this.level().getEntity(this.entityData.get(DATA_ATTACK_TARGET));
    if (entity instanceof LivingEntity living) {
      this.clientSideCachedAttackTarget = living;
      return living;
    }

    return null;
  }

  private void setAttackTarget(@Nullable LivingEntity target) {

    this.setTarget(target);
    this.entityData.set(DATA_ATTACK_TARGET, target != null ? target.getId() : 0);
  }

  @Override
  public void tick() {
    super.tick();

    if (!(this.level() instanceof ServerLevel)) return;
    if (this.isDeadOrDying()) return;

    LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;

    boolean ownerCritical = owner != null && !owner.isDeadOrDying()
        && owner.getHealth() <= owner.getMaxHealth() * CRITICAL_HEALTH_FRACTION;

    if (ownerCritical) {
      this.setAttackTarget(null);
      if (this.tickCount % 5 == 0) {
        healOwner();
      }
      if (this.tickCount % 20 == 0) {
        healSelf();
      }
      return;
    }

    if (this.tickCount % 5 == 0 || !isValidAttackTarget(this.getTarget(), owner)) {
      acquireAttackTarget(owner);
    }

    if (this.hasActiveAttackTarget()) {
      tickAttack(owner);
      return;
    }

    if (this.tickCount % 20 == 0) {
      healOwner();
      if (this.tickCount % 40 == 0) {
        healSelf();
      }
    }
  }

  private void acquireAttackTarget(@Nullable LivingEntity owner) {

    if (owner == null) {
      this.setAttackTarget(null);
      return;
    }

    LivingEntity selfLastAttacker = this.getLastHurtByMob();
    if (isValidAttackTarget(selfLastAttacker, owner)) {
      if (selfLastAttacker != this.getTarget()) {
        this.setAttackTarget(selfLastAttacker);
        this.attackCooldown = ATTACK_INTERVAL;
      }
      return;
    }

    LivingEntity ownerLastAttacker = owner.getLastHurtByMob();
    if (isValidAttackTarget(ownerLastAttacker, owner)) {
      if (ownerLastAttacker != this.getTarget()) {
        this.setAttackTarget(ownerLastAttacker);
        this.attackCooldown = ATTACK_INTERVAL;
      }
      return;
    }

    List<Monster> nearby = owner.level().getEntitiesOfClass(Monster.class, owner.getBoundingBox().inflate(ATTACK_RANGE),
        candidate -> isValidAttackTarget(candidate, owner));

    LivingEntity best = nearby.isEmpty() ? null : Collections.min(nearby,
        Comparator.<Monster>comparingDouble(LivingEntity::getMaxHealth).reversed()
            .thenComparingDouble(owner::distanceToSqr));

    if (best != this.getTarget()) {
      this.setAttackTarget(best);
      this.attackCooldown = ATTACK_INTERVAL;
    }
  }

  private boolean isValidAttackTarget(@Nullable LivingEntity target, @Nullable LivingEntity owner) {

    if (target == null || owner == null) return false;
    if (target == this || target == owner) return false;
    if (!target.isAlive() || target.isRemoved()) return false;
    if (target.level() != this.level()) return false;
    if (owner.distanceToSqr(target) > ATTACK_RANGE * ATTACK_RANGE) return false;

    if (target.getType() == owner.getType() && owner.getType() != EntityType.PLAYER) return false;

    return this.hasLineOfSight(target);
  }

  private void tickAttack(@Nullable LivingEntity owner) {

    LivingEntity target = this.getTarget();
    if (!isValidAttackTarget(target, owner)) {
      this.setAttackTarget(null);
      return;
    }

    if (this.attackCooldown > 0) {
      --this.attackCooldown;
      return;
    }

    float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    if (target.hurt(this.damageSources().indirectMagic(this, this), damage)) {
      target.setLastHurtByMob(this);
    }

    if (this.level() instanceof ServerLevel level) {
      level.playSound(null, this.blockPosition(), ModSounds.SPECTER_BEAM, SoundSource.NEUTRAL);
    }

    this.attackCooldown = ATTACK_INTERVAL;
  }

  /// so our ghosts don't slowly die over time without healing
  private void healSelf() {

    if (!(this.level() instanceof ServerLevel level)) {
      return;
    }
    if (this.isDeadOrDying()) {
      return;
    }

    boolean healed = false;

    if (this.getHealth() < this.getMaxHealth() * 0.5f) {
      this.heal(1.0f);
      healed = true;
    }

    if (!healed) {
      return;
    }

    level.playSound(null, this.blockPosition(), ModSounds.SPECTER_SHELL, SoundSource.AMBIENT);
  }

  /// lore: Ghosts channel the Traveler's Light to mend flesh and cure poisons; only while close enough to be "shielding" the owner
  private void healOwner() {

    if (!(this.level() instanceof ServerLevel)) {
      return;
    }
    if (!(this.getOwner() instanceof LivingEntity owner)) {
      return;
    }
    if (owner.isDeadOrDying()) {
      return;
    }
    if (this.distanceTo(owner) >= 4.0f) {
      return;
    }

    boolean healed = false;

    if (owner.getHealth() < owner.getMaxHealth() * 0.5f) {
      owner.heal(2.0f);
      healed = true;
    }

    if (owner.removeEffect(MobEffects.POISON)) {
      healed = true;
    }

    if (!healed) {
      return;
    }

    if (!owner.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
      owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 5, 0, true, true));
    }

    owner.level().playSound(null, this.blockPosition(), ModSounds.SPECTER_SHELL, SoundSource.AMBIENT);
  }
}
