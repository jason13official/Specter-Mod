// Made with Blockbench 5.0.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class SpecterModelRewrite<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "spectermodelrewrite"), "main");
	private final ModelPart core;
	private final ModelPart shell;
	private final ModelPart shield;

	public SpecterModelRewrite(ModelPart root) {
		this.core = root.getChild("core");
		this.shell = root.getChild("shell");
		this.shield = root.getChild("shield");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition core = partdefinition.addOrReplaceChild("core", CubeListBuilder.create().texOffs(24, 40).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(24, 32).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-2.0F, -6.0F, -4.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition cube_r1 = core.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 51).addBox(-0.975F, -3.975F, -0.525F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(2.25F, -2.0F, 2.95F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = core.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(8, 51).addBox(-0.975F, -3.975F, -0.525F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(-2.95F, -2.0F, 2.25F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r3 = core.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 51).addBox(-0.975F, -3.975F, -0.525F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(-2.95F, -2.0F, -2.25F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r4 = core.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(50, 48).addBox(-0.975F, -3.975F, -0.525F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(2.25F, -2.0F, -2.95F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r5 = core.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 47).addBox(-0.475F, -2.975F, -2.025F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(3.65F, -5.55F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r6 = core.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(10, 44).addBox(-0.475F, -2.975F, -2.025F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(-1.55F, -0.35F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r7 = core.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 44).addBox(-0.475F, -2.975F, -2.025F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(1.55F, -0.35F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r8 = core.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(40, 40).addBox(-0.475F, -2.975F, -2.025F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(-3.65F, -5.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r9 = core.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(50, 44).addBox(-1.975F, -2.975F, -0.525F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -0.35F, -1.55F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r10 = core.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(50, 40).addBox(-1.975F, -2.975F, -0.525F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -0.3F, 1.55F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r11 = core.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(48, 36).addBox(-1.975F, -2.975F, -0.525F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -5.5F, 3.65F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r12 = core.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(48, 32).addBox(-1.975F, -2.975F, -0.525F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -5.5F, -3.65F, -0.7854F, 0.0F, 0.0F));

		PartDefinition shell = partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(24, 52).addBox(-8.0F, -2.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 52).addBox(6.0F, -2.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(50, 53).addBox(-8.0F, -2.0F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 54).addBox(6.0F, -2.0F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(-8.0F, -16.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 56).addBox(6.0F, -16.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 56).addBox(-8.0F, -16.0F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 56).addBox(6.0F, -16.0F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition shield = partdefinition.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		core.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		shell.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		shield.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}