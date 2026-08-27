package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.client.renderer.SpecterRenderer;
import io.github.jason13official.specter.impl.client.screen.SpecterScreen;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModMenus;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class SpecterClientNeoForge {

  public SpecterClientNeoForge(final IEventBus modEventBus) {

    modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> {
      SpecterClient.init();

      // event.enqueueWork(() -> MenuScreens.register(ModMenus.SPECTER, SpecterScreen::new)); // 1.20.1
    });

    modEventBus.addListener((Consumer<RegisterMenuScreensEvent>) event -> {

      event.register(ModMenus.SPECTER, SpecterScreen::new);
    });

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
