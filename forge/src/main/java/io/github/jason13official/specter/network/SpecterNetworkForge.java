package io.github.jason13official.specter.network;

import io.github.jason13official.specter.SpecterMod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class SpecterNetworkForge {

  private static final String PROTOCOL_VERSION = "1";

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      SpecterMod.identifier("main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals);

  public static void register() {

    CHANNEL.registerMessage(0, RenameSpecterPacket.class, RenameSpecterPacket::encode, RenameSpecterPacket::new, RenameSpecterPacket::handle);
  }
}
