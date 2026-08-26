package io.github.jason13official.specter.impl.client.screen;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.menu.SpecterMenu;
import io.github.jason13official.specter.impl.common.network.SpecterNetworking;
import io.github.jason13official.specter.platform.Services;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class SpecterScreen extends AbstractContainerScreen<SpecterMenu> {

  public static final ResourceLocation TEXTURE = SpecterMod.identifier("textures/gui/specter.png");

  public static final int PORTRAIT_X = 8;
  public static final int PORTRAIT_Y = 20;
  public static final int PORTRAIT_SIZE = 64;
  public static final int PORTRAIT_SCALE = 120;

  public static final int STATS_X = 84;
  public static final int STATS_Y = 24;
  public static final int LINE_HEIGHT = 12;

  public static final int NAME_BOX_Y = 108;
  public static final int NAME_BOX_HEIGHT = 16;

  private EditBox nameBox;

  public SpecterScreen(SpecterMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);

    this.imageWidth = 200;
    this.imageHeight = 166;
  }

  @Override
  protected void init() {

    super.init();

    AbstractSpecter specter = this.menu.getSpecter();

    this.nameBox = new EditBox(this.font, this.leftPos + 8, this.topPos + NAME_BOX_Y, this.imageWidth - 16, NAME_BOX_HEIGHT, Component.translatable("gui.specter.name"));
    this.nameBox.setMaxLength(SpecterNetworking.MAX_NAME_LENGTH);
    this.nameBox.setValue(specter != null && specter.hasCustomName() ? specter.getName().getString() : "");
    this.nameBox.setEditable(specter != null);
    this.addRenderableWidget(this.nameBox);

    Button renameButton = Button.builder(Component.translatable("gui.specter.rename"), button -> this.onRename()).bounds(this.leftPos + (this.imageWidth - 80) / 2, this.topPos + 130, 80, 20).build();
    renameButton.active = specter != null;
    this.addRenderableWidget(renameButton);
  }

  private void onRename() {

    Services.network().sendRenameSpecter(this.nameBox.getValue());
  }

  @Override
  protected void containerTick() {

    super.containerTick();

    this.nameBox.tick();
  }

  /// super closes the screen on the inventory key (`e` default);
  /// mimic `AnvilScreen` does, so typing a bound key doesn't close the menu
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      this.minecraft.player.closeContainer();
      return true;
    }

    if (this.nameBox.keyPressed(keyCode, scanCode, modifiers) || this.nameBox.canConsumeInput()) {
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    AbstractSpecter specter = this.menu.getSpecter();

    if (specter == null) {
      guiGraphics.drawString(this.font, Component.translatable("gui.specter.missing"), this.leftPos + STATS_X, this.topPos + STATS_Y, 0x404040, false);
      return;
    }

    int portraitCenterX = this.leftPos + PORTRAIT_X + PORTRAIT_SIZE / 2;
    int portraitFeetY = this.topPos + PORTRAIT_Y + PORTRAIT_SIZE - 6;
    InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, portraitCenterX, portraitFeetY, PORTRAIT_SCALE, (float) portraitCenterX - mouseX,
        (float) (portraitFeetY - PORTRAIT_SIZE / 2) - mouseY, specter);

    int x = this.leftPos + STATS_X;
    int y = this.topPos + STATS_Y;

    Component owner = specter.getOwner() != null ? specter.getOwner().getName() : Component.translatable("gui.specter.no_owner");
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.owner", owner), x, y, 0x404040, false);

    int health = Math.round(specter.getHealth());
    int maxHealth = Math.round(specter.getMaxHealth());
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.health", health, maxHealth), x, y + LINE_HEIGHT, 0x404040, false);

    int attackDamage = (int) specter.getAttributeValue(Attributes.ATTACK_DAMAGE);
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.attack_damage", attackDamage), x, y + LINE_HEIGHT * 2, 0x404040, false);

    guiGraphics.drawString(this.font, Component.translatable("gui.specter.color"), x, y + LINE_HEIGHT * 3, 0x404040, false);
    int swatchX = x + this.font.width(Component.translatable("gui.specter.color")) + 6;
    int swatchY = y + LINE_HEIGHT * 3;
    guiGraphics.fill(swatchX, swatchY, swatchX + 8, swatchY + 8, 0xFF000000 | specter.getSpecterColor());
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
  }
}
