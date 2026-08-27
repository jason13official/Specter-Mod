package io.github.jason13official.specter.network;

import io.github.jason13official.specter.impl.common.network.SpecterNetworking.RenameSpecterPayload;
import io.github.jason13official.specter.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FabricNetworkHelper implements INetworkHelper {

  @Override
  public void sendRenameSpecter(String name) {

    ClientPlayNetworking.send(new RenameSpecterPayload(name));
  }
}
