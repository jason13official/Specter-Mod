package io.github.jason13official.specter.impl.client.screen;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class SpecterScreen extends AbstractContainerScreen<SpecterMenu> {

  public static final int PORTRAIT_SIZE = 90;
  public static final int PORTRAIT_SCALE = 130;

  public static final int NAME_BOX_WIDTH = 160;
  public static final int NAME_BOX_HEIGHT = 16;

  public static final int STATS_WIDTH = 130;
  public static final int LINE_HEIGHT = 12;

  private static final int PANEL_FILL = 0xB2101820;
  private static final int PANEL_ACCENT = 0xFF4FD4FF;
  private static final int PANEL_BORDER = 0x804FD4FF;

  private EditBox nameBox;
  private Button renameButton;

  private int portraitCenterX;
  private int portraitCenterY;
  private int statsX;
  private int statsY;
  private int statsHeight;

  public SpecterScreen(SpecterMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);

    this.imageWidth = 240;
    this.imageHeight = 200;
  }

  @Override
  protected void init() {

    super.init();

    AbstractSpecter specter = this.menu.getSpecter();

    this.portraitCenterX = this.width / 2;
    this.portraitCenterY = this.height / 2 - 30;

    int nameBoxX = this.portraitCenterX - NAME_BOX_WIDTH / 2;
    int nameBoxY = this.portraitCenterY + PORTRAIT_SIZE / 2 + 20;

    this.nameBox = new EditBox(this.font, nameBoxX, nameBoxY, NAME_BOX_WIDTH, NAME_BOX_HEIGHT, Component.translatable("gui.specter.name"));
    this.nameBox.setMaxLength(SpecterNetworking.MAX_NAME_LENGTH);
    this.nameBox.setValue(specter != null && specter.hasCustomName() ? specter.getName().getString() : "");
    this.nameBox.setEditable(specter != null);
    this.addRenderableWidget(this.nameBox);

    this.renameButton = Button.builder(Component.translatable("gui.specter.rename"), button -> this.onRename())
        .bounds(this.portraitCenterX - 40, nameBoxY + NAME_BOX_HEIGHT + 8, 80, 20).build();
    renameButton.active = specter != null;
    this.addRenderableWidget(renameButton);

    this.statsHeight = 14 + LINE_HEIGHT * 4 + 8;
    this.statsX = this.portraitCenterX + PORTRAIT_SIZE / 2 + 30;
    this.statsY = this.portraitCenterY - this.statsHeight / 2;
  }

  private void onRename() {

    Services.network().sendRenameSpecter(this.nameBox.getValue());
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.nameBox.isFocused() && !this.nameBox.isMouseOver(mouseX, mouseY)) {
      this.nameBox.setFocused(false);
    }

    if (this.renameButton.isFocused() && !this.renameButton.isMouseOver(mouseX, mouseY)) {
      this.renameButton.setFocused(false);
    }

    return super.mouseClicked(mouseX, mouseY, button);
  }

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

    this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

    AbstractSpecter specter = this.menu.getSpecter();

    if (specter == null) {
      guiGraphics.drawCenteredString(this.font, Component.translatable("gui.specter.missing"), this.portraitCenterX, this.portraitCenterY, 0xFFFFFF);
      return;
    }

    int lookTargetX;
    int lookTargetY;
    if (this.nameBox.isFocused()) {
      lookTargetX = this.nameBox.getScreenX(this.nameBox.getCursorPosition());
      lookTargetY = this.nameBox.getY() + this.nameBox.getHeight() / 2;
    } else {
      lookTargetX = mouseX;
      lookTargetY = mouseY;
    }

    int x1 = this.portraitCenterX - PORTRAIT_SIZE / 2;
    int y1 = this.portraitCenterY - PORTRAIT_SIZE / 2;
    int x2 = this.portraitCenterX + PORTRAIT_SIZE / 2;
    int y2 = this.portraitCenterY + PORTRAIT_SIZE / 2;

    InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, PORTRAIT_SCALE, 0.0625F, lookTargetX, lookTargetY, specter);

    this.drawFloatingPanel(guiGraphics, this.statsX, this.statsY, STATS_WIDTH, this.statsHeight);

    int x = this.statsX + 8;
    int y = this.statsY + 8;

    Component owner = specter.getOwner() != null ? specter.getOwner().getName() : Component.translatable("gui.specter.no_owner");
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.owner", owner), x, y, 0xE0E0E0, false);

    int health = Math.round(specter.getHealth());
    int maxHealth = Math.round(specter.getMaxHealth());
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.health", health, maxHealth), x, y + LINE_HEIGHT, 0xE0E0E0, false);

    int attackDamage = (int) specter.getAttributeValue(Attributes.ATTACK_DAMAGE);
    guiGraphics.drawString(this.font, Component.translatable("gui.specter.attack_damage", attackDamage), x, y + LINE_HEIGHT * 2, 0xE0E0E0, false);

    guiGraphics.drawString(this.font, Component.translatable("gui.specter.color"), x, y + LINE_HEIGHT * 3, 0xE0E0E0, false);
    int swatchX = x + this.font.width(Component.translatable("gui.specter.color")) + 6;
    int swatchY = y + LINE_HEIGHT * 3;
    guiGraphics.fill(swatchX, swatchY, swatchX + 8, swatchY + 8, 0xFF000000 | specter.getSpecterColor());
  }

  private void drawFloatingPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {

    guiGraphics.fill(x, y, x + width, y + height, PANEL_FILL);
    guiGraphics.fill(x, y, x + width, y + 2, PANEL_ACCENT);
    guiGraphics.fill(x, y, x + 1, y + height, PANEL_BORDER);
    guiGraphics.fill(x + width - 1, y, x + width, y + height, PANEL_BORDER);
    guiGraphics.fill(x, y + height - 1, x + width, y + height, PANEL_BORDER);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
  }
}
