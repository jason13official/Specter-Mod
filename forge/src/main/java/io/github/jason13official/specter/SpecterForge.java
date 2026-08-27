package io.github.jason13official.specter;

import io.github.jason13official.specter.impl.common.entity.Specter;
import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import io.github.jason13official.specter.impl.common.loot.ForgeLootModifiers;
import io.github.jason13official.specter.impl.common.registry.ModBlocks;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import io.github.jason13official.specter.impl.common.registry.ModItems;
import io.github.jason13official.specter.impl.common.registry.ModMenus;
import io.github.jason13official.specter.impl.common.registry.ModParticles;
import io.github.jason13official.specter.impl.common.registry.ModSounds;
import io.github.jason13official.specter.impl.common.registry.ModTabs;
import io.github.jason13official.specter.impl.common.registry.ModTiles;
import io.github.jason13official.specter.impl.common.util.ModConfigIO;
import io.github.jason13official.specter.network.SpecterNetworkForge;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class SpecterForge {

  public static IEventBus EVENT_BUS;

  public SpecterForge(final FMLJavaModLoadingContext context) {
    EVENT_BUS = context.getModEventBus();

    bind(Registries.BLOCK, ModBlocks::register);
    bind(Registries.ENTITY_TYPE, ModEntities::register);
    bind(Registries.ITEM, ModItems::register);
    bind(Registries.PARTICLE_TYPE, ModParticles::register);
    bind(Registries.BLOCK_ENTITY_TYPE, ModTiles::register);
    bind(Registries.MENU, ModMenus::register);
    bind(Registries.SOUND_EVENT, ModSounds::register);
    bind(Registries.CREATIVE_MODE_TAB, ModTabs::register);

    ForgeLootModifiers.register(EVENT_BUS);

    SpecterNetworkForge.register();

    EVENT_BUS.addListener((Consumer<FMLCommonSetupEvent>) event -> SpecterMod.init());

    EVENT_BUS.addListener((Consumer<EntityAttributeCreationEvent>) event -> {
      event.put(ModEntities.SPECTER, Specter.createAttributes().build());
      event.put(ModEntities.CUBE, Specter.createAttributes().build());
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<AddReloadListenerEvent>) event -> {
      event.addListener(new ResourceReloadListener());
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerLoggedInEvent>) event -> {
      if (event.getEntity() instanceof ServerPlayer player) {
        SpecterEvents.onPlayerLoggedIn(player);
      }
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.PlayerLoggedOutEvent>) event -> {
      if (event.getEntity() instanceof ServerPlayer player) {
        SpecterEvents.onPlayerLoggedOut(player);
      }
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<EntityJoinLevelEvent>) event -> {
      if (event.getLevel() instanceof ServerLevel level) {
        SpecterEvents.onEntityJoin(event.getEntity(), level);
      }
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<EntityLeaveLevelEvent>) event -> {
      if (event.getLevel() instanceof ServerLevel level) {
        SpecterEvents.onEntityLeave(event.getEntity(), level);
      }
    });

    if (FMLLoader.getDist() == Dist.CLIENT) {
      new SpecterClientForge(EVENT_BUS);
    }
  }

  @Deprecated
  @SuppressWarnings("all")
  public SpecterForge() {
    this(FMLJavaModLoadingContext.get());
  }

  public <T> void bind(ResourceKey<Registry<T>> registryKey, Consumer<BiConsumer<T, ResourceLocation>> source) {

    EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
      if (registryKey.equals(event.getRegistryKey())) {
        source.accept((t, rl) -> event.register(registryKey, rl, () -> t));
      }
    });
  }

  public static class ResourceReloadListener extends SimplePreparableReloadListener<Void> {

    @Override
    public String getName() {
      return SpecterMod.identifier(Constants.MOD_ID).toString();
    }

    @Override
    protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      ModConfigIO.loadOrInitialize();
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      return null;
    }
  }
}