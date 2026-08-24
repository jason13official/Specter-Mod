package io.github.jason13official.specter.impl.common.util;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import io.github.jason13official.monolib.platform.Services;
import io.github.jason13official.specter.Constants;
import io.github.jason13official.specter.impl.common.config.ServerModConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigIO {

  public static void loadOrInitialize() {

    Path configDir = Services.PLATFORM.getConfigDirectory();
    File configDirectory = new File(configDir.toUri());

    if (!configDirectory.isDirectory() && !configDirectory.mkdirs()) {

      Constants.LOG.info("Unable to get or create config directory {}", configDirectory.getAbsolutePath());

      return;
    }

    loadOrInitCommon(configDir, Constants.MOD_ID + "-common.toml");
    if (Services.PLATFORM.isClientSide()) {
      loadOrInitClient(configDir, Constants.MOD_ID + "-client.toml");
    }
    loadOrInitServer(configDir, Constants.MOD_ID + "-server.toml");
  }

  private static void loadOrInitCommon(Path configDir, String filename) {

    Path configFilepath = configDir.resolve(filename);
  }

  private static void loadOrInitClient(Path configDir, String filename) {

    Path configFilepath = configDir.resolve(filename);
  }

  private static void loadOrInitServer(Path configDir, String filename) {

    Path configFilepath = configDir.resolve(filename);
    File configFile = new File(configFilepath.toUri());

    try (CommentedFileConfig config = CommentedFileConfig.builder(configFile).build()) {

      if (Files.exists(configFilepath)) {
        config.load();
      }

      // get from config file -> runtime
      // ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.set(config.get(ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.key()));
      ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.set(getFloatOrElse(config, ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.key(), ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.get()));

      // set from runtime -> config file
      config.setComment(ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.key(), ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.comment());
      config.set(ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.key(), ServerModConfig.SPECTERS_FOR_ZOMBIES_CHANCE.get());

      config.save();

    } catch (Exception e) {

      Constants.LOG.info("Failed to load or save {}", configFile.getAbsolutePath());
      Constants.LOG.info(e.getMessage());
      e.printStackTrace();
    }
  }

  /// read as raw number and convert to float or return default if it was null
  private static float getFloatOrElse(UnmodifiableConfig config, String key, float defaultValue) {

    Number value = config.get(key);
    return value == null ? defaultValue : value.floatValue();
  }
}
