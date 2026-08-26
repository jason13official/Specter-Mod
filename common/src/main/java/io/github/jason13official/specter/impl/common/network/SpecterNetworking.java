package io.github.jason13official.specter.impl.common.network;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/// loader-agnostic packet ids + handling; each platform wires its own transport
/// (`ServerPlayNetworking`/`SimpleChannel`) to [SpecterNetworking#handleRenameSpecter]
public class SpecterNetworking {

  public static final ResourceLocation RENAME_SPECTER_PACKET = SpecterMod.identifier("rename_specter");
  public static final int MAX_NAME_LENGTH = 32;

  /// server-side; only ever renames the sender's own Specter, so no ownership check is needed
  public static void handleRenameSpecter(ServerPlayer player, String name) {

    AbstractSpecter specter = AbstractSpecter.findOwned(player.serverLevel(), player);
    if (specter == null) return;

    String trimmed = name.strip();

    if (trimmed.isEmpty()) {
      specter.setCustomName(null);
      return;
    }

    if (trimmed.length() > MAX_NAME_LENGTH) {
      trimmed = trimmed.substring(0, MAX_NAME_LENGTH);
    }

    specter.setCustomName(Component.literal(trimmed));
  }
}
