package io.github.jason13official.specter.platform.services;

public interface INetworkHelper {

  /// client -> server; applies the new custom name to the sender's owned Specter
  void sendRenameSpecter(String name);
}
