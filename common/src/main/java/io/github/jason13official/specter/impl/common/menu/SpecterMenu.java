package io.github.jason13official.specter.impl.common.menu;

import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpecterMenu extends AbstractContainerMenu {

  private final Player player;
  private final @Nullable AbstractSpecter specter;

  public SpecterMenu(int containerId, Inventory playerInventory) {
    super(ModMenus.SPECTER, containerId);

    this.player = playerInventory.player;
    // resolved independently on both sides: server-authoritative,
    // the client looks up the same specter for rendering (like horses)
    this.specter = AbstractSpecter.findOwned(this.player.level(), this.player);
  }

  public @Nullable AbstractSpecter getSpecter() {

    return this.specter;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int i) {

    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {

    return !player.isDeadOrDying() && !player.isSpectator() && this.specter != null && !this.specter.isRemoved();
  }
}
