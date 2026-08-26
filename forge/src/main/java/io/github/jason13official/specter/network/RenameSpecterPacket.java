package io.github.jason13official.specter.network;

import io.github.jason13official.specter.impl.common.network.SpecterNetworking;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class RenameSpecterPacket {

  private final String name;

  public RenameSpecterPacket(String name) {

    this.name = name;
  }

  public RenameSpecterPacket(FriendlyByteBuf buf) {

    this.name = buf.readUtf(SpecterNetworking.MAX_NAME_LENGTH);
  }

  public void encode(FriendlyByteBuf buf) {

    buf.writeUtf(this.name, SpecterNetworking.MAX_NAME_LENGTH);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {

    NetworkEvent.Context ctx = context.get();

    ctx.enqueueWork(() -> {
      ServerPlayer player = ctx.getSender();
      if (player != null) {
        SpecterNetworking.handleRenameSpecter(player, this.name);
      }
    });

    ctx.setPacketHandled(true);
  }
}
