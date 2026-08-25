package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.client.renderer.SpecterRenderer;
import io.github.jason13official.specter.impl.client.screen.SpecterScreen;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class SpecterClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    SpecterClient.init();

    ColorProviderRegistry.ITEM.register(SpecterClient.DYED_ITEM_COLOR_FN::apply, ModItems.CONDENSED_SPECTER);

    EntityModelLayerRegistry.registerModelLayer(SpecterModel.LAYER_LOCATION, SpecterModel::createBodyLayer);
    EntityRendererRegistry.register(ModEntities.SPECTER, SpecterRenderer::new);

    MenuScreens.register(ModMenus.SPECTER, SpecterScreen::new);
  }
}
