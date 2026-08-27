package io.github.jason13official.specter.impl.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jason13official.specter.impl.common.entity.Specter;
import io.github.jason13official.specter.impl.common.registry.ModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class SpecterModelOLD extends EntityModel<Specter> {

  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ModEntities.SPECTER_ID, "main");

  private ModelPart body;
  private ModelPart shell;

  private boolean shellRendered = false;

  public SpecterModelOLD(final ModelPart root) {
    construct(this, root);
  }

  private static void construct(SpecterModelOLD model, ModelPart root) {
    model.body = root.getChild("body");
    model.shell = root.getChild("shell");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();

    defineModel(root, mesh);

    return LayerDefinition.create(mesh, 64, 64);
  }

  /// hot-swappable ?
  private static void defineModel(PartDefinition root, MeshDefinition mesh) {
    PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -3.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 4.0F, 0.0F));
    PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
    PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

    PartDefinition shell = root.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 4.0F, 0.0F));
  }

  public boolean isShellRendered() {

    return this.shell != null && this.shellRendered;
  }

  public void setShellRendered(boolean value) {

    this.shellRendered = value;
  }

  public ModelPart getBody() {

    return this.body;
  }

  public ModelPart getShell() {

    return this.shell;
  }

  @Override
  public void setupAnim(@NotNull Specter specter, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    this.setShellRendered(specter.getOwner() != null && specter.distanceTo(specter.getOwner()) < 4.0f);
  }

  @Override
  public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {

    staticRenderToBuffer(this, poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
  }

  private static void staticRenderToBuffer(SpecterModelOLD model, @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
    if (model.body != null) model.body.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);

    if (model.shell != null && model.isShellRendered()) {
      model.shell.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
  }
}
