package io.github.jason13official.specter.impl.common.item;

import net.minecraft.world.item.Item;

public class SpecterCoreItem extends Item {

  public SpecterCoreItem(Properties properties) {
    super(properties);
  }

//  @Override
//  public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//
//    if (!entity.isOnFire()) {
//      if (entity.tickCount % 20 == 0 && level.getRandom().nextInt(0, 1000) == 0) {
//        entity.setSecondsOnFire(1);
//      }
//    }
//
//    super.inventoryTick(stack, level, entity, slotId, isSelected);
//  }
}
