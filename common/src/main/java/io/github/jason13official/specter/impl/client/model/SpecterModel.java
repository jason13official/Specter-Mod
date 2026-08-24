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

public class SpecterModel extends EntityModel<Specter> {

  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ModEntities.SPECTER_ID, "main");

  // private final ModelPart root;
  private final ModelPart body;
  private final ModelPart shell;

  private boolean shellRendered = false;

  public SpecterModel(final ModelPart root) {
    // this.root = root;
    this.body = root.getChild("body");
    this.shell = root.getChild("shell");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();

    PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -3.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 4.0F, 0.0F));
    PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
    PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

    PartDefinition shell = root.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 4.0F, 0.0F));

    return LayerDefinition.create(mesh, 64, 64);
  }

  public boolean isShellRendered() {

    return this.shellRendered;
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

    // TODO impl util class
    // if (SpecterHelper.hasOwnedSpecterNearby(specter, specter.getOwner(), true)) {
    this.setShellRendered(specter.getOwner() != null && specter.distanceTo(specter.getOwner()) < 4.0f);
  }


  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color) {

    this.body.render(poseStack, vertexConsumer, light, overlay, color);

    if (this.isShellRendered()) {
      this.shell.render(poseStack, vertexConsumer, light, overlay, color);
    }
  }
}
