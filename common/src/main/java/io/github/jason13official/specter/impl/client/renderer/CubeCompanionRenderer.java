package io.github.jason13official.specter.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.client.model.CubeCompanionModel;
import io.github.jason13official.specter.impl.client.model.SpecterModel;
import io.github.jason13official.specter.impl.common.entity.CubeCompanion;
import io.github.jason13official.specter.impl.common.entity.Specter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CubeCompanionRenderer extends EntityRenderer<CubeCompanion> implements RenderLayerParent<CubeCompanion, EntityModel<CubeCompanion>> {

  public static final ResourceLocation TEXTURE_LOCATION = SpecterMod.identifier("textures/entity/cube_companion.png");

  // private final SpecterModel model;

  ModelPart root;
  private CubeCompanionModel model;

  public CubeCompanionRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.root = context.bakeLayer(CubeCompanionModel.LAYER_LOCATION);
    this.model = new CubeCompanionModel(this.root);
  }

  @Override
  public @NotNull CubeCompanionModel getModel() {

    return this.model;
  }

  @Override
  public @NotNull ResourceLocation getTextureLocation(CubeCompanion specter) {

    return TEXTURE_LOCATION;
  }

  @Override
  public boolean shouldRender(CubeCompanion livingEntity, Frustum camera, double camX, double camY, double camZ) {

    return true;
  }

  /// @see net.minecraft.client.renderer.entity.BoatRenderer
  @Override
  public void render(CubeCompanion specter, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

    this.model = new CubeCompanionModel(this.root);

    doRender(specter, this, partialTick, poseStack, buffer, packedLight);

    super.render(specter, entityYaw, partialTick, poseStack, buffer, packedLight);
  }

  private static void doRender(CubeCompanion specter, CubeCompanionRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    poseStack.pushPose();

    // BoatRenderer mimics
//    poseStack.translate(0.0F, 0.0625f, 0.0F); // specific to boat model?

    // rotate yaw to face view direction (notice we are subtracting from 180.0F here,
    // which inverts the rotation. why? I have no clue.)
    float bodyYaw = Mth.rotLerp(partialTick, specter.yBodyRotO, specter.yBodyRot);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw)); // enables left/right turning

    // rotate pitch to match view direction (notice we are not subtracting from 180.0F here,
    // instead using a negative value. why? I have no clue.)
     poseStack.mulPose(Axis.XP.rotationDegrees(-specter.getViewXRot(1.0f))); // enables up/down turning

    // invert x and y axes so textures display properly
    poseStack.scale(-1.0F, -1.0F, 1.0F);

    // translate the model upwards by half it's height to re-center
    // in the bounding box
    poseStack.translate(0.0F, 0.0625f * -8.0f, 0.0F);


    renderer.getModel().setupAnim(specter, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);

    ResourceLocation texture = renderer.getTextureLocation(specter);
    float[] diffuseColors = specter.getDiffuseSpecterColors();

    renderer.getModel().getBody().render(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), packedLight, OverlayTexture.NO_OVERLAY,
        1.0f, 1.0f, 1.0f, 1.0f);

    poseStack.popPose();
  }
}
