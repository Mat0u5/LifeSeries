package net.mat0u5.lifeseries.entity.snail;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
//? if <= 1.21 {
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
public class SnailModel<T extends Snail> extends HierarchicalModel<T> {
//?} else {
/*import net.minecraft.client.model.EntityModel;
public class SnailModel extends EntityModel<SnailRenderState> {
*///?}

    //? if >= 1.21.6 {
    /*private final Animation flyAnimation;
    private final Animation glideAnimation;
    private final Animation walkAnimation;
    private final Animation idleAnimation;
    private final Animation startFlyAnimation;
    private final Animation stopFlyAnimation;
    *///?}

    public static final ModelLayerLocation SNAIL = new ModelLayerLocation(Snail.ID, "main");

    private final ModelPart main;
    private final ModelPart head;
    private final ModelPart trivia;
    private final ModelPart body;
    private final ModelPart shell;
    private final ModelPart back;
    private final ModelPart mid;
    private final ModelPart midfront;
    private final ModelPart midback;
    private final ModelPart propeller;
    private final ModelPart top;
    private final ModelPart parachute;
    private final ModelPart strings;
    public SnailModel(ModelPart root) {
        //? if >= 1.21.2 {
        /*super(root);
        *///?}
        this.main = root.getChild("main");
        this.head = this.main.getChild("head");
        this.trivia = this.head.getChild("trivia");
        this.body = this.main.getChild("body");
        this.shell = this.body.getChild("shell");
        this.back = this.body.getChild("back");
        this.mid = this.body.getChild("mid");
        this.midfront = this.mid.getChild("midfront");
        this.midback = this.mid.getChild("midback");
        this.propeller = this.body.getChild("propeller");
        this.top = this.propeller.getChild("top");
        this.parachute = this.body.getChild("parachute");
        this.strings = this.parachute.getChild("strings");

        //? if >= 1.21.6 {
        /*flyAnimation = SnailAnimations.fly.createAnimation(root);
        glideAnimation = SnailAnimations.glide.createAnimation(root);
        walkAnimation = SnailAnimations.walk.createAnimation(root);
        idleAnimation = SnailAnimations.idle.createAnimation(root);
        startFlyAnimation = SnailAnimations.startFly.createAnimation(root);
        stopFlyAnimation = SnailAnimations.stopFly.createAnimation(root);
        *///?}
    }
    public static LayerDefinition getTexturedModelData() {
        //? if <= 1.21.4 {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition main = modelPartData.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = main.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 57).addBox(-2.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 53).addBox(-2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 57).addBox(-2.0F, -2.0F, 1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 58).addBox(1.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition trivia = head.addOrReplaceChild("trivia", CubeListBuilder.create().texOffs(0, 51).addBox(-3.0F, -8.0F, 3.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = trivia.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(34, 58).addBox(-1.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r2 = trivia.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 58).addBox(0.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, 0.3927F));

        PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition shell = body.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(34, 17).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition back = body.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 58).addBox(-2.0F, -2.0F, 4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mid = body.addOrReplaceChild("mid", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition midfront = mid.addOrReplaceChild("midfront", CubeListBuilder.create().texOffs(34, 37).addBox(-1.99F, -1.99F, -4.5F, 3.98F, 1.98F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition midback = mid.addOrReplaceChild("midback", CubeListBuilder.create().texOffs(16, 51).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition propeller = body.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(32, 53).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 58).addBox(-0.5F, -12.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition top = propeller.addOrReplaceChild("top", CubeListBuilder.create().texOffs(34, 47).addBox(-3.0F, -12.01F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition parachute = body.addOrReplaceChild("parachute", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -13.6F, -4.0F, 16.0F, 0.6F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(34, 33).addBox(-7.99F, -13.0F, -3.99F, 15.98F, 0.5F, 0.5F, new CubeDeformation(0.0F))
                .texOffs(34, 35).addBox(-7.99F, -13.0F, 11.49F, 15.98F, 0.5F, 0.5F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(7.5F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-8.0F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -4.0F));

        PartDefinition strings = parachute.addOrReplaceChild("strings", CubeListBuilder.create().texOffs(44, 58).addBox(1.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(58, 37).addBox(3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 58).addBox(3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 42).addBox(-3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 58).addBox(-3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 58).addBox(-2.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(60, 37).addBox(1.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(58, 47).addBox(-2.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(modelData, 128, 128);
        //?} else {
        /*ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData head = main.addChild("head", ModelPartBuilder.create().uv(28, 57).cuboid(-2.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 53).cuboid(-2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
		.uv(16, 57).cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(12, 58).cuboid(1.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData trivia = head.addChild("trivia", ModelPartBuilder.create().uv(0, 51).cuboid(-3.0F, -8.0F, 3.0F, 6.0F, 5.0F, 2.0F, new Dilation(0.01F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = trivia.addChild("cube_r1", ModelPartBuilder.create().uv(34, 58).cuboid(-1.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, -0.3927F));

		ModelPartData cube_r2 = trivia.addChild("cube_r2", ModelPartBuilder.create().uv(32, 58).cuboid(0.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, 0.3927F));

		ModelPartData body = main.addChild("body", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData shell = body.addChild("shell", ModelPartBuilder.create().uv(34, 17).cuboid(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData back = body.addChild("back", ModelPartBuilder.create().uv(0, 58).cuboid(-2.0F, -2.0F, 4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData mid = body.addChild("mid", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData midfront = mid.addChild("midfront", ModelPartBuilder.create().uv(34, 37).cuboid(-1.99F, -1.99F, -4.5F, 3.98F, 1.98F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData midback = mid.addChild("midback", ModelPartBuilder.create().uv(16, 51).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData propeller = body.addChild("propeller", ModelPartBuilder.create().uv(32, 53).cuboid(-2.0F, -10.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(36, 58).cuboid(-0.5F, -12.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData top = propeller.addChild("top", ModelPartBuilder.create().uv(34, 47).cuboid(-3.0F, -12.01F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData parachute = body.addChild("parachute", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -13.6F, -4.0F, 16.0F, 0.6F, 16.0F, new Dilation(0.0F))
		.uv(34, 33).cuboid(-7.99F, -13.0F, -3.99F, 15.98F, 0.5F, 0.5F, new Dilation(0.0F))
		.uv(34, 35).cuboid(-7.99F, -13.0F, 11.49F, 15.98F, 0.5F, 0.5F, new Dilation(0.0F))
		.uv(0, 17).cuboid(7.5F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new Dilation(0.0F))
		.uv(0, 34).cuboid(-8.0F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -4.0F));

		ModelPartData strings = parachute.addChild("strings", ModelPartBuilder.create().uv(44, 58).cuboid(1.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(58, 37).cuboid(3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(40, 58).cuboid(3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(58, 42).cuboid(-3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(42, 58).cuboid(-3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(46, 58).cuboid(-2.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(60, 37).cuboid(1.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(58, 47).cuboid(-2.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
        *///?}
    }

    //? if <= 1.21 {
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.clientData.flyAnimationState, SnailAnimations.fly, ageInTicks);
        this.animate(entity.clientData.glideAnimationState, SnailAnimations.glide, ageInTicks);
        this.animate(entity.clientData.walkAnimationState, SnailAnimations.walk, ageInTicks);
        this.animate(entity.clientData.idleAnimationState, SnailAnimations.idle, ageInTicks);
        this.animate(entity.clientData.startFlyAnimationState, SnailAnimations.startFly, ageInTicks);
        this.animate(entity.clientData.stopFlyAnimationState, SnailAnimations.stopFly, ageInTicks);

        boolean parachuteHidden = !entity.clientData.glideAnimationState.isStarted();
        boolean propellerHidden = !entity.clientData.flyAnimationState.isStarted() && !entity.clientData.startFlyAnimationState.isStarted();
        boolean triviaHidden = !entity.isFromTrivia();

        this.parachute.getAllParts().forEach(part -> part.skipDraw = parachuteHidden);
        this.propeller.getAllParts().forEach(part -> part.skipDraw = propellerHidden);
        //this.trivia.traverse().forEach(part -> part.hidden = triviaHidden);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        main.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart root() {
        return main;
    }
    //?} else {
    /*@Override
    public void setupAnim(SnailRenderState state) {
        super.setupAnim(state);

        //? if <= 1.21.5 {
        this.animate(state.flyAnimationState, SnailAnimations.fly , state.ageInTicks);
        this.animate(state.glideAnimationState, SnailAnimations.glide , state.ageInTicks);
        this.animate(state.walkAnimationState, SnailAnimations.walk , state.ageInTicks);
        this.animate(state.idleAnimationState, SnailAnimations.idle , state.ageInTicks);
        this.animate(state.startFlyAnimationState, SnailAnimations.startFly , state.ageInTicks);
        this.animate(state.stopFlyAnimationState, SnailAnimations.stopFly , state.ageInTicks);
        //?} else {
        /^this.flyAnimation.apply(state.flyAnimationState, state.age);
        this.glideAnimation.apply(state.glideAnimationState, state.age);
        this.walkAnimation.apply(state.walkAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);
        this.startFlyAnimation.apply(state.startFlyAnimationState, state.age);
        this.stopFlyAnimation.apply(state.stopFlyAnimationState, state.age);
        ^///?}

        boolean parachuteHidden = !state.glideAnimationState.isStarted();
        boolean propellerHidden = !state.flyAnimationState.isStarted() && !state.startFlyAnimationState.isStarted();
        boolean triviaHidden = !state.fromTrivia;

        this.parachute.getAllParts().forEach(part -> part.skipDraw = parachuteHidden);
        this.propeller.getAllParts().forEach(part -> part.skipDraw = propellerHidden);
        //this.trivia.traverse().forEach(part -> part.hidden = triviaHidden);
    }
    *///?}
}