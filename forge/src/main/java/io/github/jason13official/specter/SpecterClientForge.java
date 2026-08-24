package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.client.renderer.SpecterRenderer;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import java.util.function.Consumer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class SpecterClientForge {

  public SpecterClientForge(final IEventBus modEventBus) {

    modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> SpecterClient.init());

    modEventBus.addListener((Consumer<RegisterColorHandlersEvent.Item>) event -> {
      event.register(SpecterClient.DYED_ITEM_COLOR_FN::apply, ModItems.CONDENSED_SPECTER);
    });

    modEventBus.addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> {
      event.registerLayerDefinition(SpecterModel.LAYER_LOCATION, SpecterModel::createBodyLayer);
    });

    modEventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> {
      event.registerEntityRenderer(ModEntities.SPECTER, SpecterRenderer::new);
    });
  }
}
