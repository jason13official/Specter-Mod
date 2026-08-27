package io.github.jason13official.specter.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SpecterRenderer extends EntityRenderer<Specter> implements RenderLayerParent<Specter, EntityModel<Specter>> {

  public static final ResourceLocation TEXTURE_LOCATION = SpecterMod.identifier("textures/entity/specter.png");

  private static final RenderType BEAM_RENDER_TYPE = RenderType.lightning();

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

  @Override
  public void render(Specter specter, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

    poseStack.pushPose();

    poseStack.translate(0.0F, 0.0625F + 4.0F / 16.0F, 0.0F);

    LivingEntity attackTarget = specter.hasActiveAttackTarget() ? specter.getActiveAttackTarget() : null;

    float rawYaw;
    float rawPitch;

    if (attackTarget != null) {

      double eyeY = specter.getEyeY();
      Vec3 from = getRenderPos(specter, eyeY, partialTick);
      Vec3 to = getRenderPos(attackTarget, attackTarget.getBbHeight() * 0.5, partialTick);
      Vec3 diff = to.subtract(from);
      double horizontalDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);

      rawYaw = (float) (Mth.atan2(diff.z, diff.x) * (180D / Math.PI)) - 90.0F;
      rawPitch = (float) (-(Mth.atan2(diff.y, horizontalDist) * (180D / Math.PI)));
    } else {
      rawYaw = entityYaw;
      rawPitch = specter.getViewXRot(1.0f);
    }

    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rawYaw));
    poseStack.mulPose(Axis.XP.rotationDegrees(180.0f - rawPitch));
    poseStack.scale(-1.0F, -1.0F, 1.0F);
    this.getModel().setupAnim(specter, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);

    ResourceLocation texture = this.getTextureLocation(specter);
    int color = specter.getSpecterColor();

    this.getModel().getBody().render(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), packedLight, OverlayTexture.NO_OVERLAY, color);

    if (this.getModel().isShellRendered()) {
      this.getModel().getShell().render(poseStack, buffer.getBuffer(RenderType.entityTranslucentEmissive(texture)), packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    poseStack.popPose();

    if (attackTarget != null) {
      renderAttackBeam(specter, attackTarget, poseStack, buffer, partialTick);
    }

    super.render(specter, entityYaw, partialTick, poseStack, buffer, packedLight);
  }

  private static void renderAttackBeam(Specter specter, LivingEntity target, PoseStack poseStack, MultiBufferSource buffer, float partialTick) {

    double eyeY = specter.getEyeY();

    Vec3 from = getRenderPos(specter, eyeY, partialTick);
    Vec3 to = getRenderPos(target, target.getBbHeight() * 0.5, partialTick);
    Vec3 diff = to.subtract(from);

    float length = (float) diff.length();
    if (length < 0.01f) return;

    Vec3 dir = diff.scale(1.0 / length);

    poseStack.pushPose();
    poseStack.translate(0.0, eyeY, 0.0);

    float yaw = (float) Math.atan2(dir.z, dir.x);
    float pitch = (float) Math.acos(Mth.clamp(dir.y, -1.0, 1.0));
    poseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - yaw) * (180F / (float) Math.PI)));
    poseStack.mulPose(Axis.XP.rotationDegrees(pitch * (180F / (float) Math.PI)));

    VertexConsumer consumer = buffer.getBuffer(BEAM_RENDER_TYPE);
    Matrix4f matrix = poseStack.last().pose();

    float pulse = 0.85F + 0.15F * Mth.sin((specter.tickCount + partialTick) * 1.1F);

    renderBeamBox(consumer, matrix, 0.05F, length, 1.0F, 1.0F, 1.0F, 0.25F * pulse);
    renderBeamBox(consumer, matrix, 0.02F, length, 1.0F, 1.0F, 1.0F, 0.9F * pulse);

    poseStack.popPose();
  }

  private static Vec3 getRenderPos(LivingEntity entity, double yOffset, float partialTick) {

    double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
    double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) + yOffset;
    double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
    return new Vec3(x, y, z);
  }

  private static void renderBeamBox(VertexConsumer consumer, Matrix4f matrix, float halfWidth, float length, float r, float g, float b, float a) {

    float h = halfWidth;

    beamVertex(consumer, matrix, h, 0, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, h, 0, h, r, g, b, a);

    beamVertex(consumer, matrix, -h, 0, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, -h, 0, -h, r, g, b, a);

    beamVertex(consumer, matrix, h, 0, h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, 0, h, r, g, b, a);

    beamVertex(consumer, matrix, -h, 0, -h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, 0, -h, r, g, b, a);
  }

  private static void beamVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {

    consumer.addVertex(matrix, x, y, z).setColor(r, g, b, a);
  }
}
