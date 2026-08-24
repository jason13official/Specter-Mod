package io.github.jason13official.specter.impl.common.event;

import io.github.jason13official.specter.api.common.util.SpecterDataHolder;
import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/// logging out snapshots and discards the owned Specter,
/// logging back in reconstructs it from that snapshot.
public class SpecterEvents {

  private static final String SPECTER_SNAPSHOT_TAG = "specter_snapshot";
  private static final String SPECTER_UUID_TAG = "specter_uuid";

  public static void onPlayerLoggedIn(ServerPlayer player) {

    CompoundTag data = ((SpecterDataHolder) player).specter$getPersistentData();
    if (!data.contains(SPECTER_SNAPSHOT_TAG)) return;

    CompoundTag snapshot = data.getCompound(SPECTER_SNAPSHOT_TAG);
    data.remove(SPECTER_SNAPSHOT_TAG);

    ServerLevel level = player.serverLevel();
    AbstractSpecter specter = ModEntities.SPECTER.create(level);
    if (specter == null) return;

    specter.load(snapshot);
    specter.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);

    claimSpecter(player, specter);

    level.addFreshEntity(specter);
  }

  public static void onPlayerLoggedOut(ServerPlayer player) {

    ServerLevel level = player.serverLevel();
    AbstractSpecter specter = AbstractSpecter.findOwned(level, player);
    if (specter == null) return;

    CompoundTag snapshot = specter.saveWithoutId(new CompoundTag());

    CompoundTag data = ((SpecterDataHolder) player).specter$getPersistentData();
    data.put(SPECTER_SNAPSHOT_TAG, snapshot);
    data.remove(SPECTER_UUID_TAG);

    specter.discard();
  }

  public static void recallSpecterIfLost(ServerPlayer player) {

    CompoundTag data = ((SpecterDataHolder) player).specter$getPersistentData();
    if (!data.hasUUID(SPECTER_UUID_TAG)) return;

    UUID specterId = data.getUUID(SPECTER_UUID_TAG);

    for (ServerLevel level : player.getServer().getAllLevels()) {
      if (level.getEntity(specterId) instanceof AbstractSpecter specter) {
        specter.teleportToOwner();
        return;
      }
    }
  }

  /// a player may only own one Specter at a time; claiming a new one (via `/summon` or
  /// reconstruction on login) discards whichever one they already had
  public static void claimSpecter(ServerPlayer player, AbstractSpecter specter) {

    if (player == null) {
      specter.setOwner(null);
      return;
    }

    AbstractSpecter existing = AbstractSpecter.findOwned(player.serverLevel(), player);
    if (existing != null && existing != specter) {
      existing.discard();
    }

    specter.setOwner(player);

    ((SpecterDataHolder) player).specter$getPersistentData().putUUID(SPECTER_UUID_TAG, specter.getUUID());
  }
}
