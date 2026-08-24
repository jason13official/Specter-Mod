package io.github.jason13official.specter.impl.common.item;

import io.github.jason13official.specter.impl.common.entity.AbstractSpecter;
import io.github.jason13official.specter.impl.common.event.SpecterEvents;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DyeableCondensedSpecterItem extends Item implements DyeableLeatherItem {

  public static final int DEFAULT_SPECTER_COLOR = 0xFFFFFFFF;

  public DyeableCondensedSpecterItem(Properties properties) {
    super(properties);
  }

  @Override
  public int getColor(ItemStack stack) {
    return DyeableCondensedSpecterItem.getColorFromStack(stack);
  }

  public static int getColorFromStack(ItemStack stack) {
    CompoundTag compoundtag = stack.getTagElement(DyeableLeatherItem.TAG_DISPLAY);
    return compoundtag != null && compoundtag.contains(DyeableLeatherItem.TAG_COLOR, Tag.TAG_ANY_NUMERIC) ? compoundtag.getInt(DyeableLeatherItem.TAG_COLOR) : DEFAULT_SPECTER_COLOR;
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

        if (stack.hasCustomHoverName()) {
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
