package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.common.entity.Specter;
import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import io.github.jason13official.specter.impl.common.loot.FabricLootModifiers;
import io.github.jason13official.specter.impl.common.network.SpecterNetworking;
import io.github.jason13official.specter.impl.common.registry.ModBlocks;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModMenus;
import io.github.jason13official.specter.impl.common.registry.ModParticles;
import io.github.jason13official.specter.impl.common.registry.ModSounds;
import io.github.jason13official.specter.impl.common.registry.ModTabs;
import io.github.jason13official.specter.impl.common.registry.ModTiles;
import io.github.jason13official.specter.impl.common.util.ModConfigIO;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class SpecterFabric implements ModInitializer {

  @Override
  public void onInitialize() {

    bind(BuiltInRegistries.BLOCK, ModBlocks::register);
    bind(BuiltInRegistries.ENTITY_TYPE, ModEntities::register);
    bind(BuiltInRegistries.ITEM, ModItems::register);
    bind(BuiltInRegistries.PARTICLE_TYPE, ModParticles::register);
    bind(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModTiles::register);
    bind(BuiltInRegistries.MENU, ModMenus::register);
    bind(BuiltInRegistries.SOUND_EVENT, ModSounds::register);
    bind(BuiltInRegistries.CREATIVE_MODE_TAB, ModTabs::register);

    SpecterMod.init();

    FabricDefaultAttributeRegistry.register(ModEntities.SPECTER, Specter.createAttributes());

    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ResourceReloadListener());

    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> SpecterEvents.onPlayerLoggedIn(handler.getPlayer()));
    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> SpecterEvents.onPlayerLoggedOut(handler.getPlayer()));

    ServerEntityEvents.ENTITY_LOAD.register(SpecterEvents::onEntityJoin);

    ServerPlayNetworking.registerGlobalReceiver(SpecterNetworking.RENAME_SPECTER_PACKET, (server, player, handler, buf, responseSender) -> {
      String name = buf.readUtf(SpecterNetworking.MAX_NAME_LENGTH);
      server.execute(() -> SpecterNetworking.handleRenameSpecter(player, name));
    });

    FabricLootModifiers.register();
  }

  public <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {

    source.accept((t, rl) -> Registry.register(registry, rl, t));
  }

  public static class ResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public ResourceLocation getFabricId() {
      return SpecterMod.identifier(Constants.MOD_ID);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
      ModConfigIO.loadOrInitialize();
    }
  }
}
