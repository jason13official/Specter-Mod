package io.github.jason13official.specter.impl.common.event;

import io.github.jason13official.specter.api.common.util.SpecterDataHolder;
import io.github.jason13official.specter.impl.common.config.ServerModConfig;
import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.DyeColor;

/// logging out snapshots and discards the owned Specter,
/// logging back in reconstructs it from that snapshot.
public class SpecterEvents {

  private static final String SPECTER_SNAPSHOT_TAG = "specter_snapshot";
  private static final String SPECTER_UUID_TAG = "specter_uuid";

  /// any zombie-family mob (they all extend Zombie)
  /// give them a small chance of its own Specter when it spawns/loads.
  /// `findOwned` guards against re-rolling when already present
  public static void onEntityJoin(Entity entity, ServerLevel level) {

    if (!(entity instanceof Zombie zombie)) return;
    if (level.getRandom().nextFloat() >= ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.get()) return;
    if (AbstractSpecter.findOwned(level, zombie) != null) return;

    AbstractSpecter specter = ModEntities.SPECTER.create(level);
    if (specter == null) return;

    // TODO legacy but I like it lol
    specter.setCustomName(Component.literal(String.valueOf(level.getRandom().nextInt(111111, 1000000)))); // 111,111 to 999,999

    specter.setSpecterColor(0xFF000000 | DyeColor.byId(level.getRandom().nextInt(16)).getTextColor());
    specter.moveTo(zombie.position());
    specter.setOwner(zombie);

    level.addFreshEntity(specter);
  }

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
