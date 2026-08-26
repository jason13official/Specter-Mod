package io.github.jason13official.specter.network;

import io.github.jason13official.specter.platform.services.INetworkHelper;

public class ForgeNetworkHelper implements INetworkHelper {

  @Override
  public void sendRenameSpecter(String name) {

    SpecterNetworkForge.CHANNEL.sendToServer(new RenameSpecterPacket(name));
  }
}
