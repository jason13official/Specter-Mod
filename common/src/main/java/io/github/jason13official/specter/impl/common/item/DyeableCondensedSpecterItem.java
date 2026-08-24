package io.github.jason13official.specter.impl.common.item;

import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

public class DyeableCondensedSpecterItem extends Item {

  public static final int DEFAULT_SPECTER_COLOR = DyedItemColor.LEATHER_COLOR;

  public DyeableCondensedSpecterItem(Properties properties) {
    super(properties);
  }

  public static int getColorFromStack(ItemStack stack) {

    return DyedItemColor.getOrDefault(stack, DEFAULT_SPECTER_COLOR);
  }

  @Override
  public boolean isFoil(ItemStack stack) {

    return true;
  }

  /// reconstructs the condensed Specter as a live, owned entity; replaces any Specter the
  /// player already owns, same as /summon command mixin
  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

    ItemStack stack = player.getItemInHand(usedHand);

    if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {

      AbstractSpecter specter = ModEntities.SPECTER.create(serverLevel);
      if (specter != null) {

        specter.moveTo(player.position());
        specter.setSpecterColor(getColorFromStack(stack));

        if (stack.has(DataComponents.CUSTOM_NAME)) {
          specter.setCustomName(stack.getHoverName());
        }

        SpecterEvents.claimSpecter(serverPlayer, specter);

        serverLevel.addFreshEntity(specter);
      }

      player.setItemInHand(usedHand, ItemStack.EMPTY);
    }

    return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
  }
}
