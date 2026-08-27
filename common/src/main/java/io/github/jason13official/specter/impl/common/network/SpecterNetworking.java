package io.github.jason13official.specter.impl.common.network;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class SpecterNetworking {

  public static final int MAX_NAME_LENGTH = 32;

  public record RenameSpecterPayload(String name) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RenameSpecterPayload> TYPE = new CustomPacketPayload.Type<>(SpecterMod.identifier("rename_specter"));

    public static final StreamCodec<FriendlyByteBuf, RenameSpecterPayload> STREAM_CODEC = CustomPacketPayload.codec(
        (payload, buf) -> buf.writeUtf(payload.name, MAX_NAME_LENGTH),
        buf -> new RenameSpecterPayload(buf.readUtf(MAX_NAME_LENGTH)));

    @Override
    public Type<? extends CustomPacketPayload> type() {

      return TYPE;
    }
  }

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
