package io.github.jason13official.specter.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.jason13official.specter.SpecterMod;
import io.github.jason13official.specter.impl.client.model.SpecterModel;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SpecterRenderer extends EntityRenderer<Specter> implements RenderLayerParent<Specter, EntityModel<Specter>> {

  public static final ResourceLocation TEXTURE_LOCATION = SpecterMod.identifier("textures/entity/specter.png");

  /// no texture, additive, and (per vanilla lightning/guardian-beam precedent) backface-culled ->
  /// the beam is built as an actual thin box rather than a billboarded quad so it reads from any angle
  private static final RenderType BEAM_RENDER_TYPE = RenderType.lightning();

  // private final SpecterModel model;

  ModelPart root;
  private SpecterModel model;

  public SpecterRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.root = context.bakeLayer(SpecterModel.LAYER_LOCATION);
    this.model = new SpecterModel(this.root);
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

    this.model = new SpecterModel(this.root);

    doRender(specter, this, partialTick, poseStack, buffer, packedLight);

    if (specter.hasActiveAttackTarget()) {
      renderAttackBeam(specter, poseStack, buffer, partialTick);
    }

    super.render(specter, entityYaw, partialTick, poseStack, buffer, packedLight);
  }

  /// small pure-white beam from the specter's eye to its attack target;
  /// two passes -> a wider dim "glow" box and a narrower near-opaque "core" box
  private static void renderAttackBeam(Specter specter, PoseStack poseStack, MultiBufferSource buffer, float partialTick) {

    LivingEntity target = specter.getActiveAttackTarget();
    if (target == null) return;

    double eyeY = specter.getEyeY();

    Vec3 from = getRenderPos(specter, eyeY, partialTick);
    Vec3 to = getRenderPos(target, target.getBbHeight() * 0.5, partialTick);
    Vec3 diff = to.subtract(from);

    float length = (float) diff.length();
    if (length < 0.01f) return;

    Vec3 dir = diff.scale(1.0 / length);

    poseStack.pushPose();
    poseStack.translate(0.0, eyeY, 0.0);

    // align local +Y with the beam direction, same as vanilla GuardianRenderer
    float yaw = (float) Math.atan2(dir.z, dir.x);
    float pitch = (float) Math.acos(Mth.clamp(dir.y, -1.0, 1.0));
    poseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - yaw) * (180F / (float) Math.PI)));
    poseStack.mulPose(Axis.XP.rotationDegrees(pitch * (180F / (float) Math.PI)));

    VertexConsumer consumer = buffer.getBuffer(BEAM_RENDER_TYPE);
    Matrix4f matrix = poseStack.last().pose();

    // a faint flicker so the beam reads as "live" rather than a static prop
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

  /// thin rectangular prism along local +Y from 0 to `length`;
  /// four explicit outward-facing side quads since `RenderType.lightning()` backface-culls
  private static void renderBeamBox(VertexConsumer consumer, Matrix4f matrix, float halfWidth, float length, float r, float g, float b, float a) {

    float h = halfWidth;

    // +X
    beamVertex(consumer, matrix, h, 0, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, h, 0, h, r, g, b, a);

    // -X
    beamVertex(consumer, matrix, -h, 0, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, -h, 0, -h, r, g, b, a);

    // +Z
    beamVertex(consumer, matrix, h, 0, h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, h, r, g, b, a);
    beamVertex(consumer, matrix, -h, 0, h, r, g, b, a);

    // -Z
    beamVertex(consumer, matrix, -h, 0, -h, r, g, b, a);
    beamVertex(consumer, matrix, -h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, length, -h, r, g, b, a);
    beamVertex(consumer, matrix, h, 0, -h, r, g, b, a);
  }

  private static void beamVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {

    consumer.vertex(matrix, x, y, z).color(r, g, b, a).endVertex();
  }

  private static void doRender(Specter specter, SpecterRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    poseStack.pushPose();

    // BoatRenderer mimics
//    poseStack.translate(0.0F, 0.0625f, 0.0F); // ?????
//    float bodyYaw = Mth.rotLerp(partialTick, specter.yBodyRotO, specter.yBodyRot);
//    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw)); // enables left/right turning
//    poseStack.mulPose(Axis.XP.rotationDegrees(180.0f - specter.getViewXRot(1.0f))); // flip around our specter to face forward

    poseStack.scale(-1.0F, -1.0F, 1.0F); // ?????

    // rotate yaw to face view direction (notice we are subtracting from 180.0F here,
    // which inverts the rotation. why? I have no clue.)
    float bodyYaw = Mth.rotLerp(partialTick, specter.yBodyRotO, specter.yBodyRot);
    poseStack.mulPose(Axis.YP.rotationDegrees(bodyYaw)); // enables left/right turning

    // rotate pitch to match view direction (notice we are not subtracting from 180.0F here,
    // instead using a negative value. why? I have no clue.)
    poseStack.mulPose(Axis.XP.rotationDegrees(180.0F - specter.getViewXRot(1.0f))); // enables up/down turning

    // translate the model upwards by half it's height to re-center
    // in the bounding box
    poseStack.translate(0.0F, 0.0625f * 3.0f, 0.0F);

    renderer.getModel().setupAnim(specter, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);

    ResourceLocation texture = renderer.getTextureLocation(specter);
    float[] diffuseColors = specter.getDiffuseSpecterColors();

    renderer.getModel().getBody().render(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), packedLight, OverlayTexture.NO_OVERLAY,
        diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0f);

    if (renderer.getModel().getShell() != null && renderer.getModel().isShellRendered()) {
      // entityTranslucentCull writes depth even where a fragment is mostly transparent, so
      // anything drawn after it (chests, water, ...) fails its depth test there and never
      // shows through; entityTranslucentEmissive is color-write only, no depth write
      renderer.getModel().getShell().render(poseStack, buffer.getBuffer(RenderType.entityTranslucentEmissive(texture)), packedLight, OverlayTexture.NO_OVERLAY,
          diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0f);
    }

    poseStack.popPose();
  }
}
