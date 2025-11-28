package net.mat0u5.lifeseries.entity.snail;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

//? if >= 1.21.6
/*import net.minecraft.client.animation.KeyframeAnimation;*/

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
    /*private final KeyframeAnimation flyAnimation;
    private final KeyframeAnimation glideAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation startFlyAnimation;
    private final KeyframeAnimation stopFlyAnimation;
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
        /*flyAnimation = SnailAnimations.fly.bake(root);
        glideAnimation = SnailAnimations.glide.bake(root);
        walkAnimation = SnailAnimations.walk.bake(root);
        idleAnimation = SnailAnimations.idle.bake(root);
        startFlyAnimation = SnailAnimations.startFly.bake(root);
        stopFlyAnimation = SnailAnimations.stopFly.bake(root);
        *///?}
    }
    public static LayerDefinition getTexturedModelData() {
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

    //? if <= 1.20.5 {
    /*@Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float i, float j, float k, float l) {
        main.render(matrices, vertexConsumer, light, overlay, i, j, k, l);
    }
    *///?} else {
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        main.render(matrices, vertexConsumer, light, overlay, color);
    }
    //?}

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
        /^this.flyAnimation.apply(state.flyAnimationState, state.ageInTicks);
        this.glideAnimation.apply(state.glideAnimationState, state.ageInTicks);
        this.walkAnimation.apply(state.walkAnimationState, state.ageInTicks);
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
        this.startFlyAnimation.apply(state.startFlyAnimationState, state.ageInTicks);
        this.stopFlyAnimation.apply(state.stopFlyAnimationState, state.ageInTicks);
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