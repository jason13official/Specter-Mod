package io.github.jason13official.specter.network;

import io.github.jason13official.specter.impl.common.network.SpecterNetworking;
import io.github.jason13official.specter.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public class FabricNetworkHelper implements INetworkHelper {

  @Override
  public void sendRenameSpecter(String name) {

    FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeUtf(name, SpecterNetworking.MAX_NAME_LENGTH);

    ClientPlayNetworking.send(SpecterNetworking.RENAME_SPECTER_PACKET, buf);
  }
}
