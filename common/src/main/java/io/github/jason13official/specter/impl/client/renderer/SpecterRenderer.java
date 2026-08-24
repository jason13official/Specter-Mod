package io.github.jason13official.specter.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.common.entity.Specter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SpecterRenderer extends EntityRenderer<Specter> implements RenderLayerParent<Specter, EntityModel<Specter>> {

  public static final ResourceLocation TEXTURE_LOCATION = SpecterMod.identifier("textures/entity/specter.png");

  private final SpecterModel model;

  public SpecterRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.model = new SpecterModel(context.bakeLayer(SpecterModel.LAYER_LOCATION));
  }

  @Override
  public @NotNull SpecterModel getModel() {

    return this.model;
  }

  @Override
  public @NotNull ResourceLocation getTextureLocation(Specter specter) {

    return TEXTURE_LOCATION;
  }

  @Override
  public boolean shouldRender(Specter livingEntity, Frustum camera, double camX, double camY, double camZ) {

    return true;
  }

  /// @see net.minecraft.client.renderer.entity.BoatRenderer
  @Override
  public void render(Specter specter, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

    poseStack.pushPose();

    // BoatRenderer mimics
    poseStack.translate(0.0F, 0.0625f, 0.0F);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
    poseStack.mulPose(Axis.XP.rotationDegrees(180.0f - specter.getViewXRot(1.0f)));
    poseStack.scale(-1.0F, -1.0F, 1.0F);
    this.getModel().setupAnim(specter, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);

    ResourceLocation texture = this.getTextureLocation(specter);
    float[] diffuseColors = specter.getDiffuseSpecterColors();

    this.getModel().getBody().render(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), packedLight, OverlayTexture.NO_OVERLAY,
        diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0f);

    if (this.getModel().isShellRendered()) {
      // entityTranslucentCull writes depth even where a fragment is mostly transparent, so
      // anything drawn after it (chests, water, ...) fails its depth test there and never
      // shows through; entityTranslucentEmissive is color-write only, no depth write
      this.getModel().getShell().render(poseStack, buffer.getBuffer(RenderType.entityTranslucentEmissive(texture)), packedLight, OverlayTexture.NO_OVERLAY,
          diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0f);
    }

    poseStack.popPose();

    super.render(specter, entityYaw, partialTick, poseStack, buffer, packedLight);
  }
}
