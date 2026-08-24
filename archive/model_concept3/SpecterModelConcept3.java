// Made with Blockbench 5.0.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class SpecterModelConcept3<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "spectermodelconcept3"), "main");
	private final ModelPart body;
	private final ModelPart core;
	private final ModelPart eye;
	private final ModelPart shell;
	private final ModelPart shield;

	public SpecterModelConcept3(ModelPart root) {
		this.body = root.getChild("body");
		this.core = this.body.getChild("core");
		this.eye = this.core.getChild("eye");
		this.shell = this.body.getChild("shell");
		this.shield = this.body.getChild("shield");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition core = body.addOrReplaceChild("core", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = core.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(-12, -6).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition cube_r2 = core.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(-12, -6).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition eye = core.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(-4, -2).addBox(-2.0F, -10.0F, 2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shell = body.addOrReplaceChild("shell", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition cube_r3 = shell.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -6.0F, -7.0F, 0.0F, -0.7854F, 0.7854F));

		PartDefinition cube_r4 = shell.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -6.0F, -7.0F, 0.0F, 0.7854F, -0.7854F));

		PartDefinition cube_r5 = shell.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -6.0F, 7.0F, 0.0F, -0.7854F, -0.7854F));

		PartDefinition cube_r6 = shell.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -6.0F, 7.0F, 0.0F, 0.7854F, 0.7854F));

		PartDefinition cube_r7 = shell.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 8.0F, -7.0F, 0.0F, -0.7854F, -0.7854F));

		PartDefinition cube_r8 = shell.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 8.0F, -7.0F, 0.0F, 0.7854F, 0.7854F));

		PartDefinition cube_r9 = shell.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 8.0F, 7.0F, 0.0F, -0.7854F, 0.7854F));

		PartDefinition cube_r10 = shell.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 8.0F, 7.0F, 0.0F, 0.7854F, -0.7854F));

		PartDefinition shield = body.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(-32, -16).addBox(-9.0F, -17.0F, -9.0F, 18.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}