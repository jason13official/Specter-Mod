package io.github.jason13official.specter.network;

import io.github.jason13official.specter.impl.common.network.SpecterNetworking.RenameSpecterPayload;
import io.github.jason13official.specter.platform.services.INetworkHelper;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeNetworkHelper implements INetworkHelper {

  @Override
  public void sendRenameSpecter(String name) {

    PacketDistributor.sendToServer(new RenameSpecterPayload(name));
  }
}
