package io.github.jason13official.specter.impl.common.menu;

import io.github.jason13official.specter.impl.common.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpecterMenu extends AbstractContainerMenu {

  public SpecterMenu(int containerId, Inventory playerInventory) {
    super(ModMenus.SPECTER, containerId);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int i) {

    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {

    return !player.isDeadOrDying() && !player.isSpectator();
  }
}
