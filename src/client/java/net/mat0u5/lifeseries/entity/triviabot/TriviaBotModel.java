package net.mat0u5.lifeseries.entity.triviabot;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

//? if >= 1.21.6
import net.minecraft.client.animation.KeyframeAnimation;

//? if <= 1.21 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
public class TriviaBotModel<T extends TriviaBot> extends HierarchicalModel<T> {
*///?} else {
import net.minecraft.client.model.EntityModel;
public class TriviaBotModel extends EntityModel<TriviaBotRenderState> {
//?}

    public static final ModelLayerLocation TRIVIA_BOT = new ModelLayerLocation(TriviaBot.ID, "triviabot");

    //? if >= 1.21.6 {
    
    private final KeyframeAnimation glideAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation countdownAnimation;
    private final KeyframeAnimation analyzingAnimation;
    private final KeyframeAnimation answerCorrectAnimation;
    private final KeyframeAnimation answerIncorrectAnimation;
    private final KeyframeAnimation snailTransformAnimation;

    private final KeyframeAnimation santaAnalyzingAnimation;
    private final KeyframeAnimation santaAnswerCorrectAnimation;
    private final KeyframeAnimation santaAnswerIncorrectAnimation;
    private final KeyframeAnimation santaFlyAnimation;
    private final KeyframeAnimation santaGlideAnimation;
    private final KeyframeAnimation santaIdleAnimation;
    private final KeyframeAnimation santaWaveAnimation;
    private final KeyframeAnimation santaWalkAnimation;
    private final KeyframeAnimation santaLandAnimation;
    private final KeyframeAnimation faceAngryAnimation;
    private final KeyframeAnimation faceHappyAnimation;
     //?}

    private final ModelPart full;
    private final ModelPart triviabot;
    private final ModelPart neckpivot;
    private final ModelPart main;
    private final ModelPart shell;
    private final ModelPart expressions;
    private final ModelPart mouth;
    private final ModelPart dots;
    private final ModelPart green;
    private final ModelPart yellow;
    private final ModelPart red;
    private final ModelPart clock;
    private final ModelPart clockhand;
    private final ModelPart processing;
    private final ModelPart one;
    private final ModelPart two;
    private final ModelPart three;
    private final ModelPart angry;
    private final ModelPart happy;
    private final ModelPart snail;
    private final ModelPart beard;
    private final ModelPart hat;
    private final ModelPart ball;
    private final ModelPart body;
    private final ModelPart righthand;
    private final ModelPart actualhand;
    private final ModelPart microphone;
    private final ModelPart umbrella;
    private final ModelPart top;
    private final ModelPart lefthand;
    private final ModelPart bag;
    private final ModelPart torso;
    private final ModelPart bottom;
    private final ModelPart bottomlarge;
    private final ModelPart legs;

    public TriviaBotModel(ModelPart root) {
        this(root, false);
    }

    public TriviaBotModel(ModelPart root, boolean hideNonAngryExpressions) {
        //? if >= 1.21.2 {
        super(root);
        //?}
        this.full = root.getChild("full");
        this.triviabot = this.full.getChild("triviabot");
        this.neckpivot = this.triviabot.getChild("neckpivot");
        this.main = this.neckpivot.getChild("main");
        this.shell = this.main.getChild("shell");
        this.expressions = this.main.getChild("expressions");
        this.mouth = this.expressions.getChild("mouth");
        this.dots = this.expressions.getChild("dots");
        this.green = this.dots.getChild("green");
        this.yellow = this.dots.getChild("yellow");
        this.red = this.dots.getChild("red");
        this.clock = this.expressions.getChild("clock");
        this.clockhand = this.clock.getChild("clockhand");
        this.processing = this.expressions.getChild("processing");
        this.one = this.processing.getChild("one");
        this.two = this.processing.getChild("two");
        this.three = this.processing.getChild("three");
        this.angry = this.expressions.getChild("angry");
        this.happy = this.expressions.getChild("happy");
        this.snail = this.expressions.getChild("snail");
        this.beard = this.main.getChild("beard");
        this.hat = this.main.getChild("hat");
        this.ball = this.hat.getChild("ball");
        this.body = this.triviabot.getChild("body");
        this.righthand = this.body.getChild("righthand");
        this.actualhand = this.righthand.getChild("actualhand");
        this.microphone = this.righthand.getChild("microphone");
        this.umbrella = this.righthand.getChild("umbrella");
        this.top = this.umbrella.getChild("top");
        this.lefthand = this.body.getChild("lefthand");
        this.bag = this.lefthand.getChild("bag");
        this.torso = this.body.getChild("torso");
        this.bottom = this.torso.getChild("bottom");
        this.bottomlarge = this.torso.getChild("bottomlarge");
        this.legs = this.body.getChild("legs");


        //? if >= 1.21.6 {
        glideAnimation = TriviaBotAnimations.glide.bake(root);
        idleAnimation = TriviaBotAnimations.idle.bake(root);
        walkAnimation = TriviaBotAnimations.walk.bake(root);
        countdownAnimation = TriviaBotAnimations.countdown.bake(root);
        analyzingAnimation = TriviaBotAnimations.analyzing.bake(root);
        answerCorrectAnimation = TriviaBotAnimations.answer_correct.bake(root);
        answerIncorrectAnimation = TriviaBotAnimations.answer_incorrect.bake(root);
        snailTransformAnimation = TriviaBotAnimations.snail_transform.bake(root);

        santaAnalyzingAnimation = TriviaBotAnimations.santa_analyzing.bake(root);
        santaAnswerCorrectAnimation = TriviaBotAnimations.santa_answer_correct.bake(root);
        santaAnswerIncorrectAnimation = TriviaBotAnimations.santa_answer_incorrect.bake(root);
        santaFlyAnimation = TriviaBotAnimations.santa_fly.bake(root);
        santaGlideAnimation = TriviaBotAnimations.santa_glide.bake(root);
        santaIdleAnimation = TriviaBotAnimations.santa_idle.bake(root);
        santaWaveAnimation = TriviaBotAnimations.santa_wave.bake(root);
        santaWalkAnimation = TriviaBotAnimations.santa_walk.bake(root);
        santaLandAnimation = TriviaBotAnimations.santa_land.bake(root);

        faceAngryAnimation = TriviaBotAnimations.face_angry.bake(root);
        faceHappyAnimation = TriviaBotAnimations.face_happy.bake(root);
        //?}

        if (hideNonAngryExpressions) {
            mouth.visible = false;
            dots.visible = false;
            clock.visible = false;
            processing.visible = false;
            happy.visible = false;
            snail.visible = false;
        }
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition full = modelPartData.addOrReplaceChild("full", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, -0.7F));

        PartDefinition triviabot = full.addOrReplaceChild("triviabot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neckpivot = triviabot.addOrReplaceChild("neckpivot", CubeListBuilder.create(), PartPose.offset(0.0F, 5.1F, 0.2F));

        PartDefinition main = neckpivot.addOrReplaceChild("main", CubeListBuilder.create().texOffs(40, 36).addBox(-5.0F, -4.5F, -2.6F, 10.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.1F, -0.2F));

        PartDefinition shell = main.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 41).addBox(-6.0F, -10.6F, 4.5F, 12.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(56, 69).addBox(-6.0F, -0.6F, -3.5F, 12.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(36, 70).addBox(-6.0F, -10.6F, -3.5F, 12.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(76, 52).addBox(5.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(38, 64).addBox(4.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(38, 66).addBox(4.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(36, 69).addBox(-5.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(38, 65).addBox(-5.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(76, 70).addBox(-6.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-6.0F, -10.6F, -3.5F, 12.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-6.02F, 0.4F, -3.48F, 12.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 41).addBox(-6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(40, 51).addBox(6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.1F, 0.2F));

        PartDefinition expressions = main.addOrReplaceChild("expressions", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -3.005F));

        PartDefinition mouth = expressions.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(28, 67).addBox(-3.0F, 1.53F, -2.61F, 6.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.005F));

        PartDefinition dots = expressions.addOrReplaceChild("dots", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition green = dots.addOrReplaceChild("green", CubeListBuilder.create().texOffs(56, 71).addBox(-4.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition yellow = dots.addOrReplaceChild("yellow", CubeListBuilder.create().texOffs(78, 78).addBox(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition red = dots.addOrReplaceChild("red", CubeListBuilder.create().texOffs(44, 79).addBox(2.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition clock = expressions.addOrReplaceChild("clock", CubeListBuilder.create().texOffs(48, 79).addBox(-1.2228F, -1.4755F, 1.7083F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.2228F, -0.0245F, -0.0033F));

        PartDefinition green_r1 = clock.addOrReplaceChild("green_r1", CubeListBuilder.create().texOffs(38, 60).addBox(0.0F, -3.5F, 0.69F, 1.0F, 3.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6228F, -0.5755F, 1.0083F, 0.0F, 0.0F, 0.3927F));

        PartDefinition clockhand = clock.addOrReplaceChild("clockhand", CubeListBuilder.create().texOffs(78, 77).addBox(0.0F, -0.4F, 1.98F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.2228F, -0.4755F, -0.2917F));

        PartDefinition processing = expressions.addOrReplaceChild("processing", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition one = processing.addOrReplaceChild("one", CubeListBuilder.create().texOffs(52, 79).addBox(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, 0.0F));

        PartDefinition two = processing.addOrReplaceChild("two", CubeListBuilder.create().texOffs(56, 79).addBox(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition three = processing.addOrReplaceChild("three", CubeListBuilder.create().texOffs(66, 79).addBox(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

        PartDefinition angry = expressions.addOrReplaceChild("angry", CubeListBuilder.create().texOffs(56, 51).addBox(-5.0F, -9.6F, -1.55F, 10.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.1F, 3.205F));

        PartDefinition happy = expressions.addOrReplaceChild("happy", CubeListBuilder.create().texOffs(8, 60).addBox(-5.0F, -4.5F, 1.4F, 10.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.305F));

        PartDefinition snail = expressions.addOrReplaceChild("snail", CubeListBuilder.create().texOffs(56, 60).addBox(-5.0F, -4.5F, -1.3F, 10.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.005F));

        PartDefinition beard = main.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(52, 82).addBox(-3.0F, 0.5F, -4.62F, 6.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hat = main.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(60, 84).addBox(-4.3F, -1.5F, -3.5F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3F, -6.1F, 3.3F, -0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r1 = hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(39, 93).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, -3.1F, 1.1F, -0.3927F, 0.0F, 0.0F));

        PartDefinition ball = hat.addOrReplaceChild("ball", CubeListBuilder.create(), PartPose.offset(2.1929F, -3.5F, 4.0071F));

        PartDefinition cube_r2 = ball.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(63, 95).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8F, -0.2F, 2.8F, -0.7854F, 0.7854F, 0.0F));

        PartDefinition body = triviabot.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 23.1F, 0.7F));

        PartDefinition righthand = body.addOrReplaceChild("righthand", CubeListBuilder.create(), PartPose.offset(-7.7F, -16.9F, 0.0F));

        PartDefinition actualhand = righthand.addOrReplaceChild("actualhand", CubeListBuilder.create().texOffs(22, 69).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition microphone = righthand.addOrReplaceChild("microphone", CubeListBuilder.create().texOffs(36, 73).addBox(-1.05F, -0.25F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 52).addBox(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5953F, 6.4269F, -5.5088F, 1.309F, 0.1309F, 0.0F));

        PartDefinition umbrella = righthand.addOrReplaceChild("umbrella", CubeListBuilder.create().texOffs(0, 52).addBox(-1.05F, -18.25F, -1.0F, 2.0F, 23.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 70).addBox(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0953F, 5.2269F, -3.9088F, 1.6581F, 0.2182F, -0.1309F));

        PartDefinition top = umbrella.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-11.3333F, -1.45F, -12.3333F, 24.0F, 1.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(76, 61).addBox(12.6667F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 74).addBox(9.6667F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(78, 75).addBox(-2.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(78, 76).addBox(-2.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(66, 78).addBox(-11.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(60, 78).addBox(12.6667F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 70).addBox(9.6667F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(76, 65).addBox(-11.3333F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 77).addBox(-11.3333F, -0.45F, -0.3333F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 56).addBox(12.6667F, -0.45F, -3.3333F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 52).addBox(-11.3333F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 60).addBox(-11.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6667F, -17.35F, 0.3333F, 0.0F, -0.7854F, 0.0F));

        PartDefinition lefthand = body.addOrReplaceChild("lefthand", CubeListBuilder.create().texOffs(8, 69).addBox(-1.9F, -1.5F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.1F, -17.4F, 0.0F));

        PartDefinition bag = lefthand.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(12, 83).addBox(-0.3474F, -3.2093F, -7.6446F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(36, 84).addBox(1.6526F, -6.2093F, -5.6446F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 8.05F, 2.45F, 1.3809F, 0.0242F, 1.0667F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(40, 25).addBox(-5.998F, -5.3F, -3.002F, 12.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.002F, -12.1F, 0.002F));

        PartDefinition bottom = torso.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(36, 71).addBox(-4.998F, -0.3F, -2.502F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(72, 36).addBox(-4.998F, -0.3F, 2.498F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(28, 60).addBox(-4.998F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(72, 42).addBox(5.002F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bottomlarge = torso.addOrReplaceChild("bottomlarge", CubeListBuilder.create().texOffs(0, 83).addBox(-4.998F, -0.3F, -3.002F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 85).addBox(-4.998F, -0.3F, 2.998F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(-4.998F, -0.3F, -3.002F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 83).addBox(5.002F, -0.3F, -3.002F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create().texOffs(72, 38).addBox(-3.9975F, 0.6F, -2.0025F, 8.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(72, 40).addBox(-3.9975F, 0.6F, 1.9975F, 8.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(44, 73).addBox(-3.9975F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 73).addBox(4.0025F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(72, 49).addBox(-3.9975F, 3.6F, -2.0025F, 8.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(72, 50).addBox(-3.9975F, 3.6F, 1.9975F, 8.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(76, 25).addBox(-3.9975F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(76, 30).addBox(4.0025F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(78, 71).addBox(4.0025F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(76, 35).addBox(-2.9975F, 5.6F, -2.0025F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(76, 51).addBox(-2.9975F, 5.6F, 1.9975F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(72, 78).addBox(-3.9975F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0025F, -10.5F, 0.0025F));
        return LayerDefinition.create(modelData, 128, 128);
    }

    //? if <= 1.21 {
    /*@Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.clientData.glideAnimationState, TriviaBotAnimations.glide, ageInTicks);
        this.animate(entity.clientData.idleAnimationState, TriviaBotAnimations.idle, ageInTicks);
        this.animate(entity.clientData.walkAnimationState, TriviaBotAnimations.walk, ageInTicks);
        this.animate(entity.clientData.countdownAnimationState, TriviaBotAnimations.countdown, ageInTicks);
        this.animate(entity.clientData.analyzingAnimationState, TriviaBotAnimations.analyzing, ageInTicks);
        this.animate(entity.clientData.answerCorrectAnimationState, TriviaBotAnimations.answer_correct, ageInTicks);
        this.animate(entity.clientData.answerIncorrectAnimationState, TriviaBotAnimations.answer_incorrect, ageInTicks);
        this.animate(entity.clientData.snailTransformAnimationState, TriviaBotAnimations.snail_transform, ageInTicks);

        this.animate(entity.clientData.santaAnalyzingAnimationState, TriviaBotAnimations.santa_analyzing, ageInTicks);
        this.animate(entity.clientData.santaAnswerCorrectAnimationState, TriviaBotAnimations.santa_answer_correct, ageInTicks);
        this.animate(entity.clientData.santaAnswerIncorrectAnimationState, TriviaBotAnimations.santa_answer_incorrect, ageInTicks);
        this.animate(entity.clientData.santaFlyAnimationState, TriviaBotAnimations.santa_fly, ageInTicks);
        this.animate(entity.clientData.santaGlideAnimationState, TriviaBotAnimations.santa_glide, ageInTicks);
        this.animate(entity.clientData.santaIdleAnimationState, TriviaBotAnimations.santa_idle, ageInTicks);
        this.animate(entity.clientData.santaWaveAnimationState, TriviaBotAnimations.santa_wave, ageInTicks);
        this.animate(entity.clientData.santaWalkAnimationState, TriviaBotAnimations.santa_walk, ageInTicks);
        this.animate(entity.clientData.santaLandAnimationState, TriviaBotAnimations.santa_land, ageInTicks);

        this.animate(entity.clientData.faceAngryAnimationState, TriviaBotAnimations.face_angry, ageInTicks);
        this.animate(entity.clientData.faceHappyAnimationState, TriviaBotAnimations.face_happy, ageInTicks);
    }

    //? if <= 1.20.5 {
    /^@Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float i, float j, float k, float l) {
        full.render(matrices, vertexConsumer, light, overlay, i, j, k, l);
    }
    ^///?} else {
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        full.render(matrices, vertexConsumer, light, overlay, color);
    }
    //?}

    @Override
    public ModelPart root() {
        return full;
    }
    *///?} else {
    @Override
    public void setupAnim(TriviaBotRenderState state) {
        super.setupAnim(state);

        //? if <= 1.21.5 {
        /*this.animate(state.glideAnimationState, TriviaBotAnimations.glide, state.ageInTicks);
        this.animate(state.idleAnimationState, TriviaBotAnimations.idle, state.ageInTicks);
        this.animate(state.walkAnimationState, TriviaBotAnimations.walk, state.ageInTicks);
        this.animate(state.countdownAnimationState, TriviaBotAnimations.countdown, state.ageInTicks);
        this.animate(state.analyzingAnimationState, TriviaBotAnimations.analyzing, state.ageInTicks);
        this.animate(state.answerCorrectAnimationState, TriviaBotAnimations.answer_correct, state.ageInTicks);
        this.animate(state.answerIncorrectAnimationState, TriviaBotAnimations.answer_incorrect, state.ageInTicks);
        this.animate(state.snailTransformAnimationState, TriviaBotAnimations.snail_transform, state.ageInTicks);

        this.animate(state.santaAnalyzingAnimationState, TriviaBotAnimations.santa_analyzing, state.ageInTicks);
        this.animate(state.santaAnswerCorrectAnimationState, TriviaBotAnimations.santa_answer_correct, state.ageInTicks);
        this.animate(state.santaAnswerIncorrectAnimationState, TriviaBotAnimations.santa_answer_incorrect, state.ageInTicks);
        this.animate(state.santaFlyAnimationState, TriviaBotAnimations.santa_fly, state.ageInTicks);
        this.animate(state.santaGlideAnimationState, TriviaBotAnimations.santa_glide, state.ageInTicks);
        this.animate(state.santaIdleAnimationState, TriviaBotAnimations.santa_idle, state.ageInTicks);
        this.animate(state.santaWaveAnimationState, TriviaBotAnimations.santa_wave, state.ageInTicks);
        this.animate(state.santaWalkAnimationState, TriviaBotAnimations.santa_walk, state.ageInTicks);
        this.animate(state.santaLandAnimationState, TriviaBotAnimations.santa_land, state.ageInTicks);

        this.animate(state.faceAngryAnimationState, TriviaBotAnimations.face_angry, state.ageInTicks);
        this.animate(state.faceHappyAnimationState, TriviaBotAnimations.face_happy, state.ageInTicks);
        *///?} else {
        this.glideAnimation.apply(state.glideAnimationState, state.ageInTicks);
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
        this.walkAnimation.apply(state.walkAnimationState, state.ageInTicks);
        this.countdownAnimation.apply(state.countdownAnimationState, state.ageInTicks);
        this.analyzingAnimation.apply(state.analyzingAnimationState, state.ageInTicks);
        this.answerCorrectAnimation.apply(state.answerCorrectAnimationState, state.ageInTicks);
        this.answerIncorrectAnimation.apply(state.answerIncorrectAnimationState, state.ageInTicks);
        this.snailTransformAnimation.apply(state.snailTransformAnimationState, state.ageInTicks);

        this.santaAnalyzingAnimation.apply(state.santaAnalyzingAnimationState, state.ageInTicks);
        this.santaAnswerCorrectAnimation.apply(state.santaAnswerCorrectAnimationState, state.ageInTicks);
        this.santaAnswerIncorrectAnimation.apply(state.santaAnswerIncorrectAnimationState, state.ageInTicks);
        this.santaFlyAnimation.apply(state.santaFlyAnimationState, state.ageInTicks);
        this.santaGlideAnimation.apply(state.santaGlideAnimationState, state.ageInTicks);
        this.santaIdleAnimation.apply(state.santaIdleAnimationState, state.ageInTicks);
        this.santaWaveAnimation.apply(state.santaWaveAnimationState, state.ageInTicks);
        this.santaWalkAnimation.apply(state.santaWalkAnimationState, state.ageInTicks);
        this.santaLandAnimation.apply(state.santaLandAnimationState, state.ageInTicks);

        this.faceAngryAnimation.apply(state.faceAngryAnimationState, state.ageInTicks);
        this.faceHappyAnimation.apply(state.faceHappyAnimationState, state.ageInTicks);
        //?}
    }
    //?}
}