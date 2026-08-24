package io.github.jason13official.specter.impl.common.entity;

import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import io.github.jason13official.specter.platform.Services;
import java.util.List;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
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

  /// disable traveling through portals; must teleport to owner cross-dimensionally
  @Override
  public boolean isOnPortalCooldown() {

    return true;
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

  /// search near ownerId's last known position;
  /// relies on [AbstractSpecter#teleportToOwner] keeping Specter near its owner
  @Nullable
  public static AbstractSpecter findOwned(ServerLevel level, LivingEntity owner) {

    List<AbstractSpecter> found = level.getEntities(EntityTypeTest.forClass(AbstractSpecter.class),
        owner.getBoundingBox().inflate(32.0D), specter -> owner.getUUID().equals(specter.getOwnerId().orElse(null)));

    return found.isEmpty() ? null : found.get(0);
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

    if (this.isRemoved()) return;

    floatTowardsOwner();

    lookAtOwner();

    if (this.level() instanceof ServerLevel level && this.owner == null) {

      if (discardTicks <= 0) {

        this.discard();
      } else {
        this.discardTicks -= 1;
      }
    }
  }

  private void teleportToOwner() {

    if (!(this.level() instanceof ServerLevel level)) return;
    if (this.owner == null) return;

    // Entity#teleportTo only moves within a level; the owner changing dimension (portal,
    // /execute in, a respawn point in another world, etc.) needs a level swap instead
    if (this.owner.level() instanceof ServerLevel ownerLevel && !level.dimension().equals(ownerLevel.dimension())) {
      relocateToLevel(ownerLevel);
      return;
    }

    // if we get too far; floatTowardsOwner() handles normal following otherwise
    if (this.distanceTo(this.owner) > 16.0f) {
      Vec3 pos = owner.position().add((level.getRandom().nextFloat() * 2) - 1, owner.getEyeHeight(), (level.getRandom().nextFloat() * 2) - 1);
      this.teleportTo(level, pos.x, pos.y, pos.z, RelativeMovement.ROTATION, 0, 0);
    }
  }

  /// move towards owner via direct pos translation; no block collision so we never get stuck
  private void floatTowardsOwner() {

    if (!(this.level() instanceof ServerLevel level)) return;
    if (this.owner == null) return;
    if (!level.dimension().equals(this.owner.level().dimension())) return;

    if (level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
      this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
    }

    // if we are in or above air (used to check if we're colliding with the ceiling)
    boolean inOrAboveAir = this.level().getBlockState(this.blockPosition()).is(BlockTags.AIR) || this.level().getBlockState(this.blockPosition().below()).is(BlockTags.AIR);

    // no block collision on the actual move;
    // just bias deltaMovement towards open space so we don't clip into the ceiling
    if (!level.noCollision(this.getBoundingBox()) && inOrAboveAir) {
      // this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
      this.setDeltaMovement(0, -0.1, 0);
    }

    boolean nearOwner = this.distanceToSqr(this.owner) < 16.0;

    // near the owner, settle towards a steady resting height instead of chasing eye
    // height; the bobbing compounds into an upward drift after fast movement
    double targetY = nearOwner ? this.owner.getY() + 1.0 : this.owner.getY() + this.owner.getEyeHeight() + 0.25;

    Vec3 toOwner = new Vec3(this.owner.getX() - this.getX(), targetY - this.getY(), this.owner.getZ() - this.getZ());
    double distSq = toOwner.lengthSqr();

    double dampening = distSq < 8.0 ? 0.5 : 1.0;

    if (distSq > 4.0) {
      this.setDeltaMovement(this.getDeltaMovement().add(toOwner.normalize().scale(dampening * dampening * 0.14)));
    } else if (nearOwner) {
      // inside the dampening area, but not at the resting height (spawned at
      // owner's feet, close but at odd height, etc.) -> nudge towards resting height
      // instead of just sitting there, where our momentum ran out
      this.setDeltaMovement(this.getDeltaMovement().add(0, Mth.clamp((targetY - this.getY()) * 0.05, -0.02, 0.02), 0));
    }

    Vec3 movement = this.getDeltaMovement();

    if (nearOwner) {
      // cap leftover vertical speed carried in from before we got close, so we don't keep
      // drifting upward for a while after the owner stops moving
      movement = new Vec3(movement.x, Mth.clamp(movement.y, -0.06, 0.06), movement.z);
      this.setDeltaMovement(movement);
    }

    this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

    this.setDeltaMovement(movement.multiply(0.49, 0.98, 0.49));
  }

  /// server-authoritative; the client interpolates rotation
  private void lookAtOwner() {

    if (!(this.level() instanceof ServerLevel)) return;
    if (this.owner == null) return;

    float maxRotDegrees = 8.0f;

    Vec3 toOwner = new Vec3(this.owner.getX() - this.getX(), this.owner.getY() + this.owner.getEyeHeight() + 0.25 - this.getY(), this.owner.getZ() - this.getZ());
    if (toOwner.lengthSqr() > 16.0) {
      maxRotDegrees = 16.0f;
    }

    this.lookAt(this.owner, maxRotDegrees, maxRotDegrees);
  }

  /// discards this instance and spawns a fresh one in the owner's level, carrying over full NBT;
  /// same snapshot/reload approach as player logout/login events in [SpecterEvents]
  private void relocateToLevel(ServerLevel targetLevel) {

    CompoundTag snapshot = this.saveWithoutId(new CompoundTag());

    Entity created = this.getType().create(targetLevel);
    if (!(created instanceof AbstractSpecter relocated)) return;

    relocated.load(snapshot);
    relocated.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(), 0.0F);
    relocated.setOwner(this.owner);

    targetLevel.addFreshEntity(relocated);

    this.discard();
  }

  /// owner UUID is synced via [AbstractSpecter#OPTIONAL_OWNER_UUID],
  /// `owner` itself is not networked, so the client has to resolve it locally.
  /// `SpecterModel` depends on it for the shell-render distance check
  private void synchronizeToOwner() {

    if (this.owner != null && this.owner.isRemoved()) {
      // stale reference: e.g. the owning player respawned into a new entity instance
      this.owner = null;
    }

    if (this.owner != null) return;

    Optional<UUID> optional = this.entityData.get(OPTIONAL_OWNER_UUID);
    if (optional.isEmpty()) return;

    UUID ownerId = optional.get();

    if (this.level() instanceof ServerLevel level) {
      ServerPlayer player = level.getServer().getPlayerList().getPlayer(ownerId);
      if (player != null) {
        this.owner = player;
        return;
      }
    }

    var nearbyLiving = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0D));
    for (LivingEntity checked : nearbyLiving) {

      if (checked.getUUID().equals(ownerId)) {
        this.owner = checked;
        break;
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
  protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {

    return true;
  }

  @Override
  public boolean isInWall() {

    return false;
  }

  @Override
  public boolean isPickable() {

    // TODO should we allow middle-clicking to get a copy?
    return false;
  }

  @Override
  public void push(Vec3 vector) {

    // no-op
  }

  @Override
  public void push(Entity entity) {

    // no-op
  }

  @Override
  public void push(double x, double y, double z) {

    // no-op
  }

  @Override
  protected void pushEntities() {

    // no-op
  }

  @Override
  protected void doPush(Entity entity) {

    // no-op
  }

  @Override
  public boolean isPushable() {

    return false;
  }

  @Override
  public boolean isPushedByFluid() {

    return false;
  }

  @Override
  public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluidTag, double motionScale) {

    return false;
  }

  @Override
  protected boolean updateInWaterStateAndDoFluidPushing() {

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
