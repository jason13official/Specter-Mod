package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.platform.Services;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// implements `TraceableEntity` instead of `OwnableEntity`; Specters MUST have an owner, non-negotiable
///
/// @see net.minecraft.world.entity.monster.Evoker Evoker and Vex relationship
public abstract class AbstractSpecter extends Mob implements TraceableEntity {

  public static final String SPECTER_OWNER_TAG = "specter_owner";
  public static final EntityDataAccessor<Optional<UUID>> OPTIONAL_OWNER_UUID = SynchedEntityData.defineId(AbstractSpecter.class, EntityDataSerializers.OPTIONAL_UUID);

  public static final String SPECTER_COLOR_TAG = "specter_color";
  public static final EntityDataAccessor<Integer> SPECTER_COLOR = SynchedEntityData.defineId(AbstractSpecter.class, EntityDataSerializers.INT);

  private LivingEntity owner;
  private int discardTicks = 20;

  public AbstractSpecter(EntityType<? extends AbstractSpecter> entityType, Level level) {
    super(entityType, level);

    this.setNoGravity(true);
  }

  // region apiStuff

  @Override
  protected void defineSynchedData(Builder builder) {
    super.defineSynchedData(builder);
    builder.define(OPTIONAL_OWNER_UUID, Optional.empty());
    builder.define(SPECTER_COLOR, 0xFF000000 | DyeColor.WHITE.getTextColor());
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);

    // the super overwrites NoGravity from the tag; re-set it
    this.setNoGravity(true);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag data) {
    super.readAdditionalSaveData(data);

    if (data.contains(SPECTER_OWNER_TAG, Tag.TAG_INT_ARRAY) && data.getIntArray(SPECTER_OWNER_TAG).length == 4) {
      this.setOwnerId(data.getUUID(SPECTER_OWNER_TAG));
    }

    if (data.contains(SPECTER_COLOR_TAG, Tag.TAG_INT)) {
      this.setSpecterColor(data.getInt(SPECTER_COLOR_TAG));
    }
  }

  @Override
  public void addAdditionalSaveData(CompoundTag data) {
    super.addAdditionalSaveData(data);

    this.getOwnerId().ifPresent(uuid -> {
      data.putUUID(SPECTER_OWNER_TAG, uuid);
    });

    data.putInt(SPECTER_COLOR_TAG, this.getSpecterColor());
  }

  @Override
  public @Nullable Entity getOwner() {

    return this.owner;
  }

  public void setOwner(LivingEntity owner) {

    if (owner == null || owner.isDeadOrDying()) {
      this.owner = null;
      this.entityData.set(OPTIONAL_OWNER_UUID, Optional.empty());
      return;
    }

    this.owner = owner;
    this.entityData.set(OPTIONAL_OWNER_UUID, Optional.of(owner.getUUID()));
  }

  public @NotNull Optional<UUID> getOwnerId() {

    return this.getEntityData().get(OPTIONAL_OWNER_UUID);
  }

  public void setOwnerId(@NotNull UUID uuid) {

    this.entityData.set(OPTIONAL_OWNER_UUID, Optional.of(uuid));
  }

  public int getSpecterColor() {

    return this.getEntityData().get(SPECTER_COLOR);
  }

  public void setSpecterColor(int color) {

    this.entityData.set(SPECTER_COLOR, color);
  }

  @Override
  public void tick() {

    super.tick();

    synchronizeToOwner();

    teleportToOwner();

    if (!this.level().isClientSide() && this.owner == null) {

      if (Services.PLATFORM.isDevelopmentEnvironment()) {
        System.out.println(this.getUUID() + " discardTicks: " + this.discardTicks);
      }

      if (discardTicks <= 0) {
        // TODO update whoever tracks the entity?
        this.discard();
      } else {
        this.discardTicks -= 1;
      }
    }
  }

  private void teleportToOwner() {

    if (!(this.level() instanceof ServerLevel level)) return;

    if (this.owner != null && this.distanceTo(this.owner) > 8.0f) {
      Vec3 pos = owner.position().add((level.getRandom().nextFloat() * 2) - 1, owner.getEyeHeight(), (level.getRandom().nextFloat() * 2) - 1);
      this.teleportTo(level, pos.x, pos.y, pos.z, RelativeMovement.ROTATION, 0, 0);
    }
  }

  private void synchronizeToOwner() {
    if (this.owner == null) {

      Optional<UUID> optional = this.entityData.get(OPTIONAL_OWNER_UUID);
      if (optional.isPresent()) {
        UUID ownerId = optional.get();

        var nearbyLiving = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0D));
        for (LivingEntity checked : nearbyLiving) {

          if (checked.getUUID().equals(ownerId)) {
            this.owner = checked;
            break;
          }
        }
      }
    }
  }

  // endregion apiStuff

  // region renderStuff

  @Override
  public boolean shouldRender(double x, double y, double z) {

    return true;
  }

  @Override
  public boolean shouldRenderAtSqrDistance(double distance) {

    return true;
  }

  @Override
  public boolean shouldShowName() {

    return this.hasCustomName();
  }

  // endregion renderStuff

  // region interactionWorldly

  @Override
  public boolean isPickable() {

    // TODO should we allow middle-clicking to get a copy?
    return false;
  }

  @Override
  public boolean canCollideWith(Entity entity) {

    return false;
  }

  @Override
  public boolean canBeCollidedWith() {

    return false;
  }

  @Override
  public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {

    return false;
  }

  @Override
  protected int calculateFallDamage(float fallDistance, float damageMultiplier) {

    return 0;
  }

  @Override
  protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {

    // no-op
  }

  // endregion interactionWorldly
}
