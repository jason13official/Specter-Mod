package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.impl.common.item.DyeableCondensedSpecterItem;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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

  /// enables middle-click / right-click interactions
  @Override
  public boolean isPickable() {

    return !this.isRemoved();
  }

  /// shift + empty main hand converts us into an item
  @Override
  protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {

    if (this.getOwner() == player && hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown()) {

      player.setItemInHand(hand, this.toCondensedItemStack());

      this.discard();
    }

    return super.mobInteract(player, hand);
  }

  private ItemStack toCondensedItemStack() {

    ItemStack stack = new ItemStack(ModItems.CONDENSED_SPECTER);

    if (this.getSpecterColor() != DyeableCondensedSpecterItem.DEFAULT_SPECTER_COLOR) {
      ((DyeableLeatherItem) stack.getItem()).setColor(stack, this.getSpecterColor());
    }

    if (this.hasCustomName()) {
      stack.setHoverName(this.getCustomName());
    }

    return stack;
  }

  @Override
  public void tick() {
    super.tick();

    if (this.tickCount % 20 == 0) {
      healOwner();
      if (this.tickCount % 40 == 0) healSelf();
    }
  }

  /// so our ghosts don't slowly die over time without healing
  private void healSelf() {

    if (!(this.level() instanceof ServerLevel level)) return;
    if (this.isDeadOrDying()) return;

    boolean healed = false;

    if (this.getHealth() < this.getMaxHealth() * 0.5f) {
      this.heal(1.0f);
      healed = true;
    }

    if (!healed) return;

    // level.playSound(null, this.blockPosition(), SoundEvents.ALLAY_THROW, SoundSource.AMBIENT, 0.6f, 0.4f);
    level.playSound(null, this.blockPosition(), ModSounds.SPECTER_SHELL, SoundSource.AMBIENT);
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

    // owner.level().playSound(null, this.blockPosition(), SoundEvents.ALLAY_THROW, SoundSource.AMBIENT, 0.6f, 0.8f);
    owner.level().playSound(null, this.blockPosition(), ModSounds.SPECTER_SHELL, SoundSource.AMBIENT);
  }
}
