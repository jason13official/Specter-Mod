package io.github.jason13official.specter.impl.client.screen;

import io.github.jason13official.specter.impl.common.menu.SpecterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SpecterScreen extends AbstractContainerScreen<SpecterMenu> {

  public SpecterScreen(SpecterMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

  }
}
