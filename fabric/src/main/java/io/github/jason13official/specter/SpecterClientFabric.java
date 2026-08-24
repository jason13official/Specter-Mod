package io.github.jason13official.specter;

import net.fabricmc.api.ClientModInitializer;

public class SpecterClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    SpecterClient.init();
  }
}
