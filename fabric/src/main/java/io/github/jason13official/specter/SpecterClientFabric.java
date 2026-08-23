package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.client.renderer.SpecterRenderer;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SpecterClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    SpecterClient.init();

    EntityModelLayerRegistry.registerModelLayer(SpecterModel.LAYER_LOCATION, SpecterModel::createBodyLayer);
    EntityRendererRegistry.register(ModEntities.SPECTER, SpecterRenderer::new);
  }
}
