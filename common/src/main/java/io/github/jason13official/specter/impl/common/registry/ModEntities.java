package io.github.jason13official.specter.impl.common.registry;

import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.common.entity.CubeCompanion;
import io.github.jason13official.specter.impl.common.entity.Specter;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

  public static ResourceLocation SPECTER_ID;
  public static EntityType<Specter> SPECTER;

  public static ResourceLocation CUBE_ID;
  public static EntityType<CubeCompanion> CUBE;

  public static void register(BiConsumer<EntityType<?>, ResourceLocation> consumer) {

    SPECTER_ID = SpecterMod.identifier("specter");
    SPECTER = EntityType.Builder.<Specter>of(Specter::new, MobCategory.MISC)
        .sized(0.0625F * 6f, 0.0625F * 6f)
        .clientTrackingRange(8)
        .build(SPECTER_ID.toString());
    consumer.accept(SPECTER, SPECTER_ID);

    CUBE_ID = SpecterMod.identifier("cube_companion");
    CUBE = EntityType.Builder.<CubeCompanion>of(CubeCompanion::new, MobCategory.MISC)
        .sized(0.0625F * 16f, 0.0625F * 16f)
        .clientTrackingRange(8)
        .build(CUBE_ID.toString());
    consumer.accept(CUBE, CUBE_ID);
  }
}
